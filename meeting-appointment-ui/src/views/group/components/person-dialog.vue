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
        <div class="group-people-title">
            <span @click="handleChangeGroupType(1)" :class=" active === 1 ? 'active' : ''">从通讯录中选择</span>/
            <span @click="handleChangeGroupType(2)" :class=" active === 2 ? 'active' : ''">从群组中选择</span>
        </div>
        <el-scrollbar height="25rem">
            <el-tree
                ref="treeRef"
                style="max-width: 100%"
                :data="props.data"
                show-checkbox
                node-key="userId"
                :props="defaultProps"
                :default-checked-keys="groupPeopleIds"
                :default-expanded-keys="groupPeopleIds"
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
    import { defineProps, defineEmits, ref, getCurrentInstance, watch } from 'vue';

    // 获取父组件传值
    const props = defineProps<{
        modelValue: boolean
        type: number;
        title: string;
        data: any[];
        groupPeopleIds: any[];
    }>();
    // 子组件向父组件传值
    const emit = defineEmits(['closeDialog', 'submitDialog']);

    // 搜索人员
    let search = ref<string>('');
    // 添加群组人员弹窗 ref
    const treeRef = ref<InstanceType<typeof ElTree>>();
    // tree 的默认配置
    const defaultProps = {
        children: 'childrenPart',
        label: 'departmentName',
    }

    const visible = ref(false) // 弹框显示
    watch(() => props.modelValue, (newValue) => {
        visible.value = newValue
    })

    let active = ref<number>(1);
    const handleChangeGroupType = (type: number) => {
        active.value = type;
    }

    /**
     * @description 获取选中人员信息
     */
     const handleChangeGroupPeople = (value: any) => {
        // 强制刷新视图
        getCurrentInstance()?.appContext.config.globalProperties.$forceUpdate();
        // 将选中的userId添加到 groupPeopleIds 中
        props.groupPeopleIds.push(value);
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

    /**
     * @description 提交数据
     */
    const submitDialog = () => {
        emit('submitDialog', props.type, treeRef);
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
        .active {
            font-size: .875rem;
            font-weight: 250;
            letter-spacing: 0;
            color: #3268dc;
        }
    }
</style>