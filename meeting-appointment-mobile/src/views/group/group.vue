<template>
  <div id="container" class="group-manage">
    <van-dialog v-model:show="addShow" title="群组信息" show-cancel-button :before-close="closeGroupDialog">
      <van-form ref="groupFormRef">
        <van-cell-group>
          <van-field 
            v-model="groupName" 
            placeholder="请输入群组名称" 
            :rules="[{ required: true, message: '请填写群组名称' }]" 
          />
          <van-field 
            v-model="groupPeopleNames" 
            left-icon="plus" 
            placeholder="请选择用户" 
            readonly 
            @click="handleAddTree" 
            :rules="[{ required: true, message: '请选择用户' }]" 
          />
        </van-cell-group>
      </van-form>
    </van-dialog>

    <PersonTreeDialog 
        v-model="addGroupVisible" 
        title="添加群组人员" 
        :type="type" 
        :peopleIds="peopleIds"
        @close-dialog="closeAddGroupDialog" 
        @submit-dialog="handleCheckedNodes" />

        <!-- <van-sticky>
            <van-nav-bar title="群组管理" left-text="返回" right-text="添加" left-arrow @click-left="onClickLeft" @click-right="onClickRight" />
        </van-sticky> -->
    <div class="group-top">
      <van-field v-model="blurValue" clearable placeholder="请输入群组名称搜索" @update:model-value="updataBlurValue" />
      <van-button type="primary" @click="onClickRight">添加</van-button>
    </div>
    <van-collapse v-model="activeNames">
      <van-collapse-item class="created" title="我创建的" name="1" ref="createdRef">
        <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" offset="200"
        :style="{ height: activeNames == '1' ? '78.05vh' : '38.33vh'}" @load="onLoad">
          <van-swipe-cell v-for="(item, index) in createdDatas">
            <div class="card">
              <div class="card-title" @click="item.isEdit == false" v-if="item.isEdit">
                <van-icon name="edit" @click="item.isEdit = false" v-show="item.createdBy == userInfo?.userId" />
                {{ item.groupName }}
              </div>
              <div class="card-title" v-else><van-field v-model="item.groupName" placeholder="请输入用户名" @blur="editGroupName(item, index)"/></div>
              <div class="card-content">
                <div class="content-tr"><van-icon name="underway" />{{ item.gmtCreate }}</div>
                <div class="content-tr"><van-icon name="user" />创建人：{{ item.userName }}</div>
                <div class="content-tr" @click="editGroupPeople(item)"><van-icon name="friends" />{{ item.users.map((item: any) => { return item.userName}).join(',') }}</div>
              </div>
            </div>
            <template #right>
              <!-- <van-button text="修改" type="primary" @click="editGroup(item)" /> -->
              <van-button text="删除" type="danger" @click="deleteGroup(item.id)" />
            </template>
          </van-swipe-cell>
        </van-list>
      </van-collapse-item>

      <van-collapse-item class="participate" title="我参与的" name="2">
        <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" offset="200"
         :style="{ height: activeNames == '2' ? '78.05vh' : '38.33vh'}" @load="onLoad">
          <van-swipe-cell v-for="item in partDatas">
            <div class="card">
              <div class="card-title">{{ item.groupName }}</div>
              <div class="card-content">
                <div class="content-tr content-time"><van-icon name="underway" />{{ item.gmtCreate }}</div>
                <div class="content-tr content-found"><van-icon name="user" />创建人：{{ item.userName }}</div>
                <div class="content-tr content-users"><van-icon name="friends" />{{ item.users.map((item: any) => { return item.userName}).join(',') }}</div>
              </div>
            </div>
          </van-swipe-cell>
        </van-list>
      </van-collapse-item>

    </van-collapse>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import  { type FormInstance, showSuccessToast, showFailToast } from 'vant';
import PersonTreeDialog from '@/views/group/components/person-tree-dialog.vue';
import { getGroupUserTree, getMeetingGroupList, deleteMeetingGroup, updateMeetingGroup, addMeetingGroup } from '@/request/api/group'
import { useRouter } from 'vue-router'

const router = useRouter();

const groupFormRef = ref<FormInstance>();
let addShow = ref(false); // 添加弹窗展示与否

let addGroupVisible = ref(false); // 成员树展示与否
let peopleIds = ref<any>([]); // 群组成员id
let groups = ref<any>([]); // 选中的群组成员信息、
let type = ref<number>(1); // 1 创建群组 2 修改群组

// let modelValue = ref() 
let listData = ref()

let blurValue = ref<string>(''); // 模糊查询群组名称
let groupName = ref<string>(''); // card-群组名称
let groupPeopleNames = ref<string>(''); // card-群组成员
const userInfo = ref<any>({});  // 用户信息
let activeNames = ref<any>(['1', '2']);  // 折叠面板 展开的面板的值

const createdDatas = ref<any>([]); // 群组列表 我创建的
const partDatas = ref<any>([]); // 群组列表 我参与的
const loading = ref(false); // 加载状态
const finished = ref(false); // 加载结束状态
const page = ref(1); // 页码
const size = ref(10); // 每页条数
const total = ref(0); // 总条数

onMounted(() => {
  userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}') // 获取用户信息
  onLoad() // 加载群组列表
})

// 模糊搜索群组名称
const updataBlurValue = (val: string) => {
  refresh()
  blurValue.value = val;
}

const onClickLeft = () => {
  router.go(-1);
}
const onClickRight = () => {
  addShow.value = true;
  getGroupUserTreeData()
}

// 点击 添加成员
const handleAddTree = () => {
  addGroupVisible.value = true;
}

const closeGroupDialog = (action:any, close: () => void) => {
    if (action === 'confirm') {
        groupFormRef.value?.validate()
        .then(() => {
          console.log('验证通过，提交表单:',);
          addMeetingGroupReq();
        })
        .catch(() => {});
    }
    if (action === 'cancel') {
        // close();
        beforeCloseGroupDialog()
    }
    
}
const addMeetingGroupReq = () => {
    addMeetingGroup({
        groupName: groupName.value,
        createdBy: userInfo.value.userId,
        userName: userInfo.value.name,
        users: groups.value,
    })
        .then(res => {
            showSuccessToast("创建成功");
            refresh();
            beforeCloseGroupDialog();
        })
        .catch(err => { })
        .finally(() => {})
}
const beforeCloseGroupDialog = () =>{
    addShow.value = false;
    groupName.value = '';
    groupPeopleNames.value = '';
    groups.value = [];
    peopleIds.value = [];
    // 清除表单校验
    groupFormRef.value?.resetValidation();
}

// 弹窗的确认按钮
// const confirm = () => {
//   onSubmit()
// }
// 树的变化
// const changeModelValue = () => {
// }

/**
 * @description 提交需要添加的群组人员
 */
 const handleCheckedNodes = (type: number, active: number, form: any) => {
    // 创建人信息
    const creator = ref<any>({ userId: userInfo.value.userId, userName: userInfo.value.name });
    console.log(form.peopleIds, "form.peopleIds");
    
    // 获取被选中成员的id
    peopleIds.value = form.peopleIds;
    // 获取被选中人员的 信息将创建人信息添加进去并去重
    groups.value = Array.from(new Set([...form.groups, creator.value].map((item: any) => JSON.stringify(item)))).map((item: any) => JSON.parse(item));

    // 创建群组
    if (type == 1) {
        // 处理被选中成员的名称 用，隔开
        groupPeopleNames.value = form.groupPeopleNames.join(',');
    }
    // 修改群组成员
    if (type == 2) {
        updateGroupInfo({ id: groupId.value, users: groups.value });
    }
    // 关闭弹窗
    closeAddGroupDialog();
}
/**
 * @description 关闭添加群组人员弹窗
 */
const closeAddGroupDialog = () => {
    addGroupVisible.value = false;
}

// 获取成员树数据
const getGroupUserTreeData = async() => {
  await getGroupUserTree()
    .then((res) => {
      listData.value = res.data;
    })
    .catch((err) => {})
}

/**
 * @description 获取群组列表
 * @param {pageNum} 页码
 * @param {pageSize} 一页10条数据
 * @param {userId} 用户id
 */
const getMeetingGroupListData = async() => {
  await getMeetingGroupList({ pageNum: page.value, pageSize: size.value, userId: userInfo.value.userId, groupName: blurValue.value })
    .then((res) => {
      total.value = res.data.length;
      partDatas.value.push(...res.data.map((item: any) => {
        item.isEdit = true;
        return item;
    }));

    res.data.forEach((item: any) => {
        if (item.createdBy === userInfo.value.userId) {
          item.isEdit = true;
          createdDatas.value.push(item);
        }
      })
    })
    .catch((err) => {})
}

const onLoad = async () => {
    if (finished.value && loading.value) return;
    loading.value = true;
    await getMeetingGroupListData();
    if (total.value < size.value) {
        return finished.value = true;
    }
    page.value++;
    loading.value = false;
};

// 增删改的时候 刷新一下列表页面 将页码变为1，鉴于列表是push的 所以将列表置空
const refresh = async() => {
  page.value = 1;
  partDatas.value = [];
  createdDatas.value = [];
  finished.value = false;
  await onLoad();
}
let groupId = ref<string>(''); // 群组id
const editGroupName = (item: any, index: number) => {
    console.log(item, createdDatas.value[index].isEdit);
    
    if (!item.groupName) {
        showFailToast('群组名称不可为空！');
        return;
    }
    createdDatas.value[index].isEdit = true;
    updateGroupInfo({ id: item.id, groupName: item.groupName });
    item.isEdit = false;
}
const editGroupPeople = (item: any) => {
  if (type.value == 1) {
        peopleIds.value = [];
    }
    type.value = 2;
    peopleIds.value = Array.from(new Set(item.users.map((item: any) => item.userId)));
    groupId.value = item.id;
    addGroupVisible.value = true;
}
/**
 * @description: 更新群组信息
 * @param {object} data 群组信息 {id: 群组id, groupName: 群组名称, users: 群组成员}
 * @return {*}
 */
 const updateGroupInfo = async (data: { id: string; groupName?: string; users?: [] }) => {
    await updateMeetingGroup(data)
        .then(res => {
            showSuccessToast('修改成功');
        })
        .catch(err => { })
        .finally(() => {
            refresh();
        })
}

 
// 删除
const deleteGroup = (id: number) => {
  deleteMeetingGroup({ id: id })
    .then(() => {
      refresh();
    })
    .catch(() => {})
}

</script>

<style lang="scss" scoped>
html {
  background: #F7F8FA;
}
.group-manage {
  // height: 100vh;
  background: #F7F8FA;
//   padding: .1rem 0;
  .group-top {
    display: flex;
    margin-bottom: .625rem;
    .van-button {
      // flex: 1;
      width: 80px;
      font-size: 1.2rem;
      // padding: 10px;
    }
  }
  // .van-cell-group {
  //   display: flex;
  //   .van-field {
  //     border: 1px solid rgba(0, 0, 0, 0.1);
  //   }

  // }
  // 折叠面板
  :deep().van-collapse {
    .van-cell {
      font-size: 1rem;
      color: #000;
      background: #FFF;
      span {
        font-weight: bolder;
      }
      i {
        color: #000;
        font-weight: bolder;
      }
    }

    .van-collapse-item__content {
      background: #F7F8FA;
      padding-top: 0;
      // max-height: 42vh;
      overflow: auto;
      .card {
        background: #FFF;
        border-radius: .5rem;
        padding: .7rem 1rem;
        margin: .7rem 0;
        .card-title {
          color: #000;
          font-size: .9rem;
          font-weight: bolder;
          // margin-bottom: .4rem;
        }
        .card-content {
          padding: .3rem 0;
          .content-tr {
            padding: .3rem 0;
            i {
              margin-right: .7rem;
            }
          }
        }
      }
      .van-swipe-cell__right {
        button {
          height: 100%;
        }
      }
    }
  }
}
</style>