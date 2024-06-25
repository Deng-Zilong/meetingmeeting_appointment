<template>
  <div ref="echartsDom" class="room-chart"></div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'; // 5.4区别4引入方式
import {onBeforeUnmount, onMounted, ref, watch} from 'vue';
import {getRoomOccupancyDate} from '@/request/api/manage';

const echartsDom = ref();
const echartsInstance = ref();
const props = defineProps({
  barData: Array
});
let result = ref<any>(); // 接收后端接口数据



const percentageFormatter = (params: any) => Math.round(params.value * 10000) / 100 + '%';

const refreshChart = () => {
  const option: any = {
  title: {
    text: '会议室占用率统计', // 图表标题
    left: 'center', // 标题位置
    top: '2%', // 标题顶部对齐
    textStyle: {
      fontSize: nowSize(20)
    },
  },
  legend: {
    selectedMode: false,
    right: '4%', // 图例右侧对齐
    top: '7%', // 图例顶部对齐
    textStyle: {
      fontSize: nowSize(15)
    },
  },
  grid: {
    top: '15%',
    bottom: '13%'
  },
  yAxis: {
    type: 'value',
    min: 0,
    max: 1, // 设置 y 轴的最大值为 1
    data: [],
    axisLabel: {
      textStyle: {
        fontSize: nowSize(15)
      },
    }
  },
  xAxis: {
    type: 'category', data: [],
    axisLabel: {
      show: true, // 是否显示刻度标签，默认显示
      interval: 0, // 坐标轴刻度标签的显示间隔，在类目轴中有效；默认会采用标签不重叠的策略间隔显示标签；可以设置成0强制显示所有标签；
      rotate: -20, // 刻度标签旋转的角度，在类目轴的类目标签显示不下的时候可以通过旋转防止标签之间重叠；旋转的角度从-90度到90度
      inside: false, // 刻度标签是否朝内，默认朝外
      margin: 11, // 刻度标签与轴线之间的距离
      textStyle: {
        fontSize: nowSize(15)
      },
    },
  },
  series: [],
  tooltip: {}
};
  // x 轴
  const xAxis = result.value.map((item: any) => item.roomName);
  option.xAxis.data = xAxis;

  const seriesItems = ['unoccupied', 'top1', 'top2', 'top3', 'others'];
  const series: any = seriesItems.map((name, sid) => ({
    name,
    type: 'bar',
    stack: 'total',
    barWidth: '60%',
    label: name === 'unoccupied' ? {show: false} : {show: true, formatter: percentageFormatter , textStyle: {fontSize: nowSize(15)} }, // 不展示 notOccupancy 的 label
    itemStyle: name === 'unoccupied' ? {color: 'rgba(180, 180, 180, 0.2)'} : {}, // 修改 notOccupancy 的颜色
    data: []
  }));
  result.value.forEach((item: any) => {
    item.timePeriods.forEach((timeItem: { occupancyRate: number }, index: number) => {
      if (timeItem.occupancyRate !== 0) {
        series[index].data.push(timeItem.occupancyRate)
      } else {
        series[index].data.push(null) // 设置为null，表示不展示
      }
    })
  })
  option.series = series.reverse(); // 倒序
  option.tooltip = {
    formatter: (seriesInfo: any) => {
      const xAxisIndex = seriesInfo.dataIndex; // x 轴索引
      const seriesIndex = seriesInfo.seriesIndex; // 维度索引

      const seriesItem = result.value[xAxisIndex]; // 维度数据
      const timeRange = seriesItem.timePeriods?.[series.length - 1 - seriesIndex]?.timePeriod; // 时间段
      const times = seriesItem.timePeriods?.[series.length - 1 - seriesIndex]?.occupied;
      const percentage = seriesItem.timePeriods?.[series.length - 1 - seriesIndex]?.occupancyRate;

      let str = seriesInfo.marker + '时间段：' + timeRange + `<br>${seriesInfo.marker}` + '使用次数：' + times + `次<br>${seriesInfo.marker}` + '百分比：' + percentageFormatter({value: percentage});
      return str;
    }
  };

  echartsInstance.value.setOption(option);

};
// 窗口大小改变时，重新设置图表字体大小
const nowSize = (val?: any, initWidth = 1920) => {
  return val / initWidth * document.documentElement.clientWidth;
} 
// 窗口大小改变时，重新设置图表大小
window.addEventListener("resize", () => {
  echartsInstance.value.resize();
  refreshChart();
});


onBeforeUnmount(() => {
  // 离开页面必须进行移除，否则会造成内存泄漏，导致卡顿
  window.removeEventListener("resize", refreshChart);
})

onMounted(async () => {
  echartsInstance.value = echarts.init(echartsDom.value);
  result.value = (await getRoomOccupancyDate({})).data;
  refreshChart();
});

// 当 roomX 或 occupied 或 total 改变时，更新图表
watch(() => [props.barData, result.value], () => {
  refreshChart();
  if (props.barData) {
    result.value = props.barData;
  }
}, {deep: true});

</script>

<style lang="scss" scoped>
.room-chart {
  width: 770px;
  height:600px;
}
</style>