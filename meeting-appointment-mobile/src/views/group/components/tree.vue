<template>
  <div class="list">
    <div class="item" v-for="item in props.list" :key="item.key" v-show="!item.isHide">
      <div class="title">
        <div class="checkbox-box">
          <van-checkbox  icon-size="16px" shape="square" @click.stop="checkChange(item)" v-model="item.checked">
            <span style="font-size: 15px;">{{ item.departmentName}}</span>
          </van-checkbox>
        </div>
        <div @click.stop="itemClick(item)" :class="item.first?'arrow':'arrowlast'">
            <van-icon v-if="item.childrenPart && item.childrenPart.length" :departmentName="item.checked ? 'arrow-up' : 'arrow-down'" />
        </div>
        
      </div>
      <div class="tree" v-show="item.first || item.checked">
        <tree  
         :isLink="data.isLink"
          v-if="item.childrenPart && item.childrenPart.length" 
          :list="item.childrenPart" 
          :listObj="props.listObj"
          :isFirstFloor="false" 
          :multiple="data.multiple" 
          @confirm="onConfirm" 
          :defaultId="defaultId">
        </tree>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { reactive, watch } from 'vue'
import tree from './tree.vue'
const emits = defineEmits(["change","confirm"])
const props = defineProps({

  // 是否是第一层
  isFirstFloor: {
    type: Boolean,
    default() {
      return true;
    },
  },
  // 树形结构
  list: {
    type: Array,
    default() {
      return [];
    },
  },
  // 树形扁平化数据
  listObj: {
    type: Object,
    default() {
      return {};
    },
  },
  // 单选默认值
  defaultId : String
})

 
const data = reactive({
  firstLoad: true,
  checkboxValue1: [],
  showList: [],
  isLink:true,
  multiple:true,
  isOutData: true, // 需要将数据抛出
})
 
watch(() => props.list, () => {
  if (data.firstLoad) {
    outDataBuffer();
    data.firstLoad = false;
  }
  // 判断 是第一层树 且 不是进行显示隐藏操作时，进行数据的抛出
  if (props.isFirstFloor && data.isOutData) {
    if(data.multiple){
      outCheckedData();
    }
  }
}, { deep: true })
 
// 展开
const itemClick = (item: any) => {
  outDataBuffer();
  item.checked = !item.checked
  
}
// 数据抛出缓冲（在list数据变化时，不想抛出选择的数据时，调用该方法）
const outDataBuffer = () => {
  data.isOutData = false;
  setTimeout(() => {
    data.isOutData = true;
  }, 500);
}
// 获取选中对象
const getCheckData = (list: any) => {
  let deptList = [];
  list.forEach((itm: any) => {
    if (itm.checked) {
      deptList.push(itm);
    }
    if (itm.childrenPart && itm.childrenPart.length) {
      deptList = deptList.concat(getCheckData(itm.childrenPart));
    }
  });
  return deptList;
}
// 单项checked改变
const checkChange = (item: any) => {

  // 多选
  if (data.multiple) {
    // item.checked = !item.checked
    if (data.isLink) {
      // 展开所有可以展开的节点
      if (item.checked) {
          expandAll(item);
      }
      // 判断父级是否需要勾选
      checkParent(item);
      // 勾选子级
      if (item.childrenPart && item.childrenPart.length) {
        checkChidren(item.childrenPart, item.checked);
        outCheckedData();
      }
    }
    return
  }
 
  // 单选
  if(item.childrenPart && item.childrenPart.length) return
  toggleAllSelectData(props.list)
  outCheckedData();

}
 
// 获取全部可选择数据，进行全选/取消
const toggleAllSelectData = (list: any) => {
  list.forEach((itm: any) => {
    itm.checked = false
    if (itm.childrenPart && itm.childrenPart.length) {
      toggleAllSelectData(itm.childrenPart)
    }
  });
}
 
// 展开所有可以展开的节点
const expandAll = (item: any) => {
  if (item.childrenPart?.length) {
    item.checked = true
    item.childrenPart.forEach((itm: any) => {
      expandAll(itm);
    })
  }
}
// 判断父级是否需要勾选
const checkParent = (item: any) => {
  // 父级不存在不再往下走
  if (!props.listObj[item[props.pidKey]]) {
    return;
  }
  let someDataCount = 0; // 同级的相同父级数据量
  let checkedDataCount = 0; // 同级已勾选的数据量
  for (const id in props.listObj) {
    const itm = props.listObj[id];
    if (itm[props.pidKey] === item[props.pidKey] && !itm.isHide) {
      someDataCount++;
      if (itm.checked) {
        checkedDataCount++;
      }
    }
  }
  const isEqual = someDataCount === checkedDataCount;
  if (props.listObj[item[props.pidKey]].checked != isEqual) {
    props.listObj[item[props.pidKey]].checked = isEqual
    checkParent(props.listObj[item[props.pidKey]]);
  }
}
// 根据父级统一取消勾选或勾选
const checkChidren = (list: any, isChecked: any) => {
  list.forEach((itm: any) => {
    itm.checked = isChecked
    if (itm.childrenPart && itm.childrenPart.length) {
      checkChidren(itm.childrenPart, isChecked);
    }
  });
}
// 抛出选中的数据
const outCheckedData = () => {
  const checkedList = getCheckData(props.list);
  emits("change", checkedList);
  onConfirm(checkedList)
}
 
const onConfirm = (e: any) => {
  emits("confirm", e);
}
 
defineExpose({
  itemClick,
  outDataBuffer,
  getCheckData,
  checkChange,
  expandAll,
  checkParent,
  checkChidren,
  outCheckedData,

})
</script>
 
<style lang="scss" scoped>
 
.list {
  .item {
    margin-bottom: 10px;
 
    .title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 10px;
 
      .checkbox-box {
        display: flex;
        align-items: center;
        cursor: pointer;
        padding: 10px 0;
      }
 
      .arrow{
        width: 80px;
        display: flex;
        justify-content: flex-end;
      }
    }
 
    .tree {
      margin-left: 50px;
    }
  }
    .arrow{
    display: none !important;
   }
}
</style>
