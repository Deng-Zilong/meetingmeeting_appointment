<template>
  <van-popup v-model:show="showPicker" round position="bottom"  @click-overlay="onClickOverlay" >
    <div class="tree-box">
      <div class="tree-container">
        <div class="tree-data">
          <TreeSelect
            ref="treeSelectRef"
            :list="data.list"
            :listObj="data.listObj"
            @confirm="onConfirm"
          ></TreeSelect>
        </div>
      </div>
    </div>

    <div class="tree-confirm">
      <van-button type="primary" block @click="handleConfirm">确定</van-button>
    </div>
  </van-popup>
</template>
 
<script setup lang="ts">
import { reactive, watch, ref, nextTick, onMounted } from "vue";
import TreeSelect from "./tree.vue";
import { showLoadingToast, closeToast } from "vant";

const emits = defineEmits(["changeModelValue", "update:show", "confirm"]);
const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
  // 绑定值
  modelValue: {
    type: Array,
     default() {
      return [];
    },
  },
  listData: {
    type: Array,
    default() {
      return [];
    },
  },
});

watch(
  () => props.show,
  () => {
    showPicker.value = props.show;
    initData(props.listData);
  }
);

const showPicker = ref(props.show);
const data = reactive({
  list: props.listData, // 树数组
  listObj: {}, // 数组对象
  selectList: [], // 选中的数据
  canCheckList: [], // 能够选择的数据集合
  canCheckListFixed: [], // 固定的能够选择的数据集合
});

const treeSelectRef = ref(null);

const init = () => {
  data.canCheckList = [];
  data.canCheckListFixed = [];
};
const initData = (options: any) => {
  if (options && options.length) {
    options[0].first=true
    data.list = options;
    init();
    data.listObj = setListObj(options);
  }
};

// 将树形数据转为扁平对象
const setListObj = (list:any) => {
  let listObj = ref();
  list.forEach((itm: any) => {
    if(props.modelValue&&props.modelValue.indexOf(itm.id)!==-1){
       itm.checked=true
    }
    data.canCheckList.push(itm);
    data.canCheckListFixed.push(itm);
    listObj[itm.id] = itm;
    if (itm.childrenPart && itm.childrenPart.length) {
      listObj = {
        ...listObj,
        ...setListObj(itm.childrenPart),
      };
    } else if(itm.treeUsers && itm.treeUsers.length) {
      listObj = { ...listObj, ...setListObj(itm.treeUsers)};
    }
  });

  return listObj;
};

const onClickOverlay = () => {
  emits("update:show", false);
};
// 确认
const handleConfirm = () => {
  emits("changeModelValue", data.selectList);
  emits("update:show", false);
};

const onConfirm = (e:any) => {
  const showSelectList = filterData(e);
  data.selectList = showSelectList.map((itm:any) => itm.id);
};

// 过滤数据
const filterData = (selectList:any) => {
  // 过滤出展示中，且打勾的数据
  const showSelectList = selectList.filter((itm:any) => {
    return !itm.isHide && !itm.isShowChildren;
  });
  return showSelectList;
};

const sendWordShow = ref(false);

defineExpose({
  init,
  setListObj,
});
</script>
 
<style lang="scss" scoped>
.tree-box {
  --van-search-content-background-color: #eeeeee;
  --van-search-content-background: #eeeeee;
}

.tree-container {
  width: 100%;
  padding: 32px 32px 0;
}

.tree-data {
  height: 60vh;
  overflow-y: auto;
}

.tree-btns {
  width: 100%;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}

.tree-confirm {
  width: 100%;
  padding: 12px 32px;
}
</style>
