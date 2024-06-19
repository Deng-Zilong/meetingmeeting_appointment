<template>
  <div class="group-Manage">

    <van-dialog v-model:show="addShow" title="群组信息" show-cancel-button>
      <van-form validate-trigger="onSubmit" >
        <van-cell-group>
          <van-field v-model="groupName" placeholder="请输入群组名称" :rules="[{ required: true, message: '请填写群组名称' }]" />
          <van-field v-model="users" left-icon="plus" placeholder="请选择用户" readonly @click="handleAddTree" :rules="[{ required: true, message: '请选择用户' }]" />
        </van-cell-group>
      </van-form>
    </van-dialog>
    <treeSelect
      ref="treeSelectRef"
      v-model:show="treeShow"
      :modelValue="modelValue"
      :listData="listData"
      :multiple='true'
      placeholder="请选择"
      @changeModelValue="changeModelValue"
    ></treeSelect>
    <!-- <van-tree-select
      :items="listData"
      :active-id.sync="modelValue"
      :main-active-index.sync="activeIndex"
    /> -->


    <van-nav-bar title="群组管理" left-text="返回" right-text="添加" left-arrow @click-left="onClickLeft" @click-right="onClickRight" />
    <van-field v-model="blurValue" clearable placeholder="请输入群组名称搜索" @update:model-value="updataBlurValue" />
    <van-collapse v-model="activeNames">
      <van-collapse-item class="created" title="我创建的" name="1" ref="createdRef">
        <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" offset="200"
        :style="{ height: activeNames == '1' ? '72.8vh' : '35.7vh'}" @load="onLoad">
          <van-swipe-cell v-for="item in createdDatas">
            <div class="card">
              <div class="card-title">{{ item.groupName }}</div>
              <div class="card-content">
                <div class="content-tr content-time"><van-icon name="underway" />{{ item.gmtCreate }}</div>
                <div class="content-tr content-found"><van-icon name="user" />创建人：{{ item.createdBy }}</div>
                <div class="content-tr content-users"><van-icon name="friends" />{{ item.users.map((item: any) => { return item.userName}).join(',') }}</div>
              </div>
            </div>
            <template #right>
              <van-button text="修改" type="primary" @click="edidGroup(item)" />
              <van-button text="删除" type="danger" @click="deleteGroup(item.id)" />
            </template>
          </van-swipe-cell>
        </van-list>
      </van-collapse-item>

      <van-collapse-item class="participate" title="我参与的" name="2">
        <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" offset="200"
         :style="{ height: activeNames == '2' ? '72.8vh' : '35.7vh'}" @load="onLoad">
          <van-swipe-cell v-for="item in partDatas">
            <div class="card">
              <div class="card-title">{{ item.groupName }}</div>
              <div class="card-content">
                <div class="content-tr content-time"><van-icon name="underway" />{{ item.gmtCreate }}</div>
                <div class="content-tr content-found"><van-icon name="user" />创建人：{{ item.createdBy }}</div>
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
import TreeSelect from '@/views/group/components/select-tree.vue';
import { getGroupUserTree, getMeetingGroupList, deleteMeetingGroup, updateMeetingGroup, addMeetingGroup } from '@/request/api/group'
import router from '@/router';

let addShow = ref(false); // 添加弹窗展示与否
let treeShow = ref(false); // 成员树展示与否
let modelValue = ref() 
let listData = ref()
// let activeIndex = ref(0)  // 原本树的左侧选项索引

let blurValue = ref<string>(''); // 模糊查询群组名称
let groupName = ref<string>(''); // card-群组名称
let users = ref<string>(); // card-群组成员
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
  treeShow.value = true;
}

// 树的变化
const changeModelValue = () => {
  
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
      partDatas.value.push(...res.data);

      res.data.map((item: any) => {
        if (item.userId = userInfo.value.userId) {
          return createdDatas.value.push(item);
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

const edidGroup = (item: any) => {
  addShow.value = true;
  groupName.value = item.groupName;
  users.value = item.users.map((item: any) => { return item.userId }).join(',');
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
.group-Manage {
  background: #F7F8FA;
  padding: .1rem 0;
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
          .content-time {}
          .content-found {}
          .content-users {}
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