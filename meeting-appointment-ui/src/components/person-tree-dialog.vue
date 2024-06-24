<template>
    <el-dialog v-model="visible" :title="title" class="add-group-dialog" width="18%" :before-close="closeDialog">
        <el-select
            v-model="search"
            clearable
            filterable
            remote
            @change="handleChangeGroupPeople"
            :remote-method="remoteSearchGroupPeople"
            style="width: 100%"
            placeholder="输入人员名称快速查找"
        >
            <el-option
                v-for="item in groupPeopleList"
                :key="item.userId"
                :label="item.userName"
                :value="item.userId"
            />
        </el-select>
        <div class="group-people-title" v-show="type == 3">
            <span @click="handleChangeGroupType(1)" :class=" active === 1 ? 'active' : ''">从通讯录中选择</span>/
            <span @click="handleChangeGroupType(3)" :class=" active === 3 ? 'active' : ''">从群组中选择</span>
        </div>
        <el-scrollbar height="400px" v-if="active != 3">
            <el-tree
                ref="treeGroupRef"
                style="max-width: 100%"
                :data="addGroupForm.list"
                show-checkbox
                node-key="userId"
                :props="defaultProps"
                :default-checked-keys="addGroupForm.peopleIds"
                :default-expanded-keys="addGroupForm.peopleIds"
            />
        </el-scrollbar>
        <el-scrollbar height="400px" v-else>
            <el-tree
                ref="treeRef"
                style="max-width: 100%"
                :data="groupList"
                show-checkbox
                node-key="userId"
                :props="defaultGroupProps"
            />
        </el-scrollbar>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="closeDialog">取消</el-button>
                <el-button type="primary" @click="submitDialog">确 认</el-button>
            </div>
        </template>
    </el-dialog>
</template>
<script lang="ts" setup>
    import { likeByName } from '@/request/api/group';
    import type { ElTree } from 'element-plus';
    import { defineProps, defineEmits, ref, getCurrentInstance, watch, onMounted, nextTick } from 'vue';
    import { getGroupUserTree } from '@/request/api/group'

    const userInfo = ref<any>(JSON.parse(localStorage.getItem('userInfo') as string) || '');
    const currentUserId = ref<string>(userInfo.value.userId);
    const currentUserName = ref<string>(userInfo.value.name);

    // 获取父组件传值
    const props = defineProps<{
        modelValue: boolean;     // 弹框显示
        type: number;            // 弹框类型 1：创建群组 2：修改群组人员 3：添加参会人员 4：添加管理员
        title: string;           // 弹框标题
        peopleIds?: any[];       // 从通讯录选中人员id
        groupList?: any[];       // 群组列表
        groupPersonIds?: any[];  // 从群组选中的人员id
    }>();
    // 子组件向父组件传值
    const emit = defineEmits(['closeDialog', 'submitDialog']);

    const visible = ref(false) // 弹框显示
    watch(() => props.modelValue, (newValue) => {
        active.value = 1; // 默认展示通讯录
        visible.value = newValue; // 打开弹窗
        addGroupForm.value.peopleIds = props.peopleIds; // 回显选中的人员
        handleAddGroupReq(); // 获取通讯录数据
    })

    // 搜索人员
    let search = ref<string>('');
    // 添加人员弹窗 ref
    const treeGroupRef = ref<InstanceType<typeof ElTree>>();
    // tree 的默认配置
    const defaultProps = {
        children: 'childrenPart',
        label: 'departmentName',
    }


    // 添加群组人员弹窗数据
    let addGroupForm = ref({
        list:<any> [],              // 通讯录列表数据
        peopleIds: <any> [],        // 选中的人员id
        groups: <any> [],           // 选中的人员信息
        groupPeopleNames: <any> [], // 选中的人员名称
    })
    
    /**
     * @description 获取群组成员数据
     */
     const handleAddGroupReq = () => {
        getGroupUserTree()
          .then(res => {
                addGroupForm.value.list = treeUserListToChildren(res.data);
            })
            .catch(err => {})
            .finally(() => {})
    }
    /**
     * @description 处理群组成员数据结构
     */
    const treeUserListToChildren = (data: any) => {
        data.forEach((item: any) => {
            // 如果当前节点有 treeUsers，则创建一个代表 treeUsers 的虚拟节点
            if (item.treeUsers && item.treeUsers.length > 0) {
              const userListNode = item.treeUsers.map((user: any) => {
                if (props.type == 4 && user.level == 2) {
                    user.departmentName = user.userName;
                    user.isTreeUsersNode = true;
                    return user;
                }
                if (props.type !== 4) {
                    user.departmentName = user.userName;
                    user.isTreeUsersNode = true;
                    return user;
                }
              }).filter((item: any) => item !== undefined)

                // 将这个虚拟节点插入到 childrenPart 的最前面
                item.childrenPart.unshift(...userListNode);
            }
            // 递归处理子节点
            if (item.childrenPart && item.childrenPart.length > 0) {
                treeUserListToChildren(item.childrenPart);
            }
        });
        return data;
    }

    // 从群组中选择人员弹窗 ref
    const treeRef = ref<InstanceType<typeof ElTree>>();
    let active = ref<number>(1); // 1.通讯录中选择 3.从群组中选择
    let groupPersonIds = ref<any>([]); // 群组选中人员id
    let groupUserNames = ref<any>([]); // 群组选中人员名称
    let groupUserInfo = ref<any>([]);  // 群组选中人员信息

    // tree 的默认配置
    const defaultGroupProps = {
        children: 'users',
        label: 'userName',
    }
    /**
     * @description 切换选择类别
     */
    const handleChangeGroupType = (type: number) => {
        // 从群组切换到通讯录
        if (type == 3 && treeGroupRef.value) {
            handleGroupTree();
        }
        // 从通讯录切换到群组
        if (type == 1 && treeRef.value) {
            handleTree();
        }
        active.value = type;
    }

    let groupPeopleList = ref<any>([]); // 远程搜索群组成员列表
    /**
     * @description 远程搜索群组成员
     */
    let times = 0; // 搜索框输入值的次数
    const remoteSearchGroupPeople = (query: string) => {
        // 当query不为空时 即 用户输入过值
        if (query != '') {
            times++;
        }
        // 当query为空 且 用户一直未输入值时 搜索框的值为null
        if(query == '' && times == 0) {
            query = 'null';
        }
        likeByName({name: query})
            .then((res: any) => {
                groupPeopleList.value = res.data;
            })
            .catch((err: any) => {
                groupPeopleList.value = [];
            })
    }
    /**
     * @description 获取选中人员信息
     */
     const handleChangeGroupPeople = (value: any) => {
        // 将选中的userId添加到 groupPeopleIds 中
        addGroupForm.value.peopleIds = Array.from(new Set([...addGroupForm.value.peopleIds, value]));
    }

    let allIds = ref<any>([]);
    let allName = ref<any>([]);
    let allInfo = ref<any>([]);
    /**
     * @description 提交数据
     */
    const submitDialog = () => {
        // 会议室预约 添加参会人员
        if ( props.type == 3) {
            // 从群组中选择
            if (active.value == 3 && treeRef.value) {
                handleTree();
            }
            // 从通讯录中选择
            if (active.value == 1 && treeGroupRef.value) {
                handleGroupTree();
            }
            // 选中的人员id去重
            allIds.value = Array.from(new Set([...groupPersonIds.value, ...addGroupForm.value.peopleIds, currentUserId.value ]));
            // 选中人员名称去重
            allName.value = Array.from(new Set([...groupUserNames.value, ...addGroupForm.value.groupPeopleNames, currentUserName.value]));
            // 选中人员信息去重
            const infoArr = [...groupUserInfo.value, ...addGroupForm.value.groups, {userId: currentUserId.value, userName: currentUserName.value}];
            allInfo.value = Array.from(
                new Map(
                    infoArr.map((item: any) => [item.userId, {
                        userId: item.userId,
                        userName: item.userName,
                    }])
                ).values()
            );
            return emit ('submitDialog',props.type, active.value, allIds.value, allName.value, allInfo.value);
            
        }
        // 群组管理 1 创建群组 2 修改群组
        if (props.type == 1 || props.type == 2) {
            handleGroupTree();
            emit(
                'submitDialog',
                props.type, 
                active.value,
                addGroupForm.value,
            );
        }
        // 后台管理 添加管理员
        if (props.type == 4) {
            handleGroupTree();
            emit(
                'submitDialog',
                props.type, 
                addGroupForm.value.peopleIds
            );
        }
    }
    // 获取选中的群组人员信息
    const handleTree = () => {
        const selectedPeople = treeRef.value!.getCheckedNodes(false, false).filter(((el: any) => el.groupName == undefined))
        // 获取被选中成员的id
        groupPersonIds.value = Array.from(new Set(selectedPeople.map((item: any) => item.userId)));
        groupUserNames.value = Array.from(new Set(selectedPeople.map((item: any) => item.userName)));
        // 获取被选中人员的 信息并去重
        groupUserInfo.value = Array.from(
            new Map(
                selectedPeople.map((item: any) => [item.userId, {
                    userId: item.userId,
                    userName: item.userName,
                }])
            ).values()
        );
    }
    // 获取选中的人员信息
    const handleGroupTree = () => {
        const selectedPeople = treeGroupRef.value!.getCheckedNodes(false, false).filter(((el: any) => el.parentId == undefined))
        // 获取被选中成员的id
        addGroupForm.value.peopleIds = Array.from(new Set(selectedPeople.map((item: any) => item.userId)));
        addGroupForm.value.groupPeopleNames = Array.from(new Set(selectedPeople.map((item: any) => item.userName)));
        // 获取被选中人员的 信息并去重
        addGroupForm.value.groups = Array.from(
            new Map(
                selectedPeople.map((item: any) => [item.userId, {
                    userId: item.userId,
                    userName: item.userName,
                }])
            ).values()
        );
    }

    /**
     * @description 关闭弹窗
     */
    const closeDialog = () => {
        emit('closeDialog');
    }

    
</script>
<style lang="scss" scoped>
    .dialog-footer{
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .el-select {
        margin-bottom: 8px;
    }
    .group-people-title {
        color: #606266;
        font-size: 14px;
        font-weight: 250;
        cursor: pointer;
        margin-bottom: 10px;
        .active {
            font-size: 14px;
            font-weight: 250;
            letter-spacing: 0;
            color: #3268dc;
        }
    }
</style>