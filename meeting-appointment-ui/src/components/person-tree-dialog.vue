<template>
    <el-dialog v-model="visible" :title="title" class="add-group-dialog" width="18.75rem">
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
        <el-scrollbar height="25rem" v-if="active != 3">
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
        <el-scrollbar height="25rem" v-else>
            <el-tree
                ref="treeRef"
                style="max-width: 100%"
                :data="groupList"
                show-checkbox
                node-key="userId"
                :props="defaultGroupProps"
                :default-checked-keys="groupPersonIds"
                :default-expanded-keys="groupPersonIds"
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

    // 获取父组件传值
    const props = defineProps<{
        modelValue: boolean
        type: number;
        title: string;
        peopleIds?: any[];
        groupList?: any[];
        groupPersonIds?: any[];
    }>();
    // 子组件向父组件传值
    const emit = defineEmits(['closeDialog', 'submitDialog']);

    // 搜索人员
    let search = ref<string>('');
    // 添加人员弹窗 ref
    const treeGroupRef = ref<InstanceType<typeof ElTree>>();
    // tree 的默认配置
    const defaultProps = {
        children: 'childrenPart',
        label: 'departmentName',
    }

    const visible = ref(false) // 弹框显示
    watch(() => props.modelValue, (newValue) => {
        visible.value = newValue;
        if (props.type == 2) {
            console.log(props.peopleIds);
            
            addGroupForm.value.peopleIds = props.peopleIds;
        }
        handleAddGroupReq();
    })

    // 添加群组人员弹窗数据
    let addGroupForm = ref({
        list:<any> [],
        peopleIds: <any> [],
        groups: <any> [],
        groupPeopleNames: <any> [],
    })
    
    /**
     * @description 获取群组成员数据
     */
     const handleAddGroupReq = () => {
        getGroupUserTree()
            .then(res => {
                addGroupForm.value.list = treeUserListToChildren(res.data);
            })
            .catch(err => {
                console.log(err, "err");
            })
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
                    user.departmentName = user.userName;
                    user.isTreeUsersNode = true;
                    return user;
                })
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
    // 添加群组人员弹窗 ref
    const treeRef = ref<InstanceType<typeof ElTree>>();
    let active = ref<number>(1);
    let groupPersonIds = ref<any>([]);
    let groupUserNames = ref<any>([]);
    let groupUserInfo = ref<any>([]);
    const defaultGroupProps = {
        children: 'users',
        label: 'userName',
    }
    const handleChangeGroupType = (type: number) => {
        if (type == 3 && treeGroupRef.value) {
            handleGroupTree();
        }
        if (type == 1 && treeRef.value) {
            handleTree();
        }
        active.value = type;
    }

    /**
     * @description 获取选中人员信息
     */
     const handleChangeGroupPeople = (value: any) => {
         const userId = ref<string>(value)
         
         // 强制刷新视图
         getCurrentInstance()?.appContext.config.globalProperties.$forceUpdate();
         // 将选中的userId添加到 groupPeopleIds 中
         addGroupForm.value.peopleIds?.push(userId.value);
         console.log(value, "value", addGroupForm.value.peopleIds, userId.value);
        }

    let groupPeopleList = ref<any>([]); // 远程搜索群组成员列表
    /**
     * @description 远程搜索群组成员
     */
    const remoteSearchGroupPeople = (query: string = 'null') => {
        likeByName({name: query})
            .then((res: any) => {
                groupPeopleList.value = res.data;
            })
            .catch((err: any) => {
                groupPeopleList.value = [];
            })
    }

    let allIds = ref<any>([]);
    let allName = ref<any>([]);
    let allInfo = ref<any>([]);
    /**
     * @description 提交数据
     */
    const submitDialog = () => {
        if ( props.type == 3) {
            if (active.value == 3 && treeRef.value) {
                handleTree();
            }
            if (active.value == 1 && treeGroupRef.value) {
                handleGroupTree();
            }
            allIds.value = [...groupPersonIds.value, ...addGroupForm.value.peopleIds];
            allName.value = [...groupUserNames.value, ...addGroupForm.value.groupPeopleNames];
            allInfo.value = [...groupUserInfo.value, ...addGroupForm.value.groups];
            return emit ('submitDialog',props.type, active.value, allIds.value, allName.value, allInfo.value);
            
        }
        if (props.type == 1 || props.type == 2) {
            handleGroupTree();
            emit(
                'submitDialog',
                props.type, 
                active.value,
                addGroupForm.value,
            );
        }
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
        margin-bottom: .5rem;
    }
    .group-people-title {
        color: #606266;
        font-size: 14px;
        font-weight: 250;
        cursor: pointer;
        margin-bottom: .625rem;
        .active {
            font-size: .875rem;
            font-weight: 250;
            letter-spacing: 0;
            color: #3268dc;
        }
    }
</style>