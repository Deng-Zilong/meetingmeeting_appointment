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
      text: '{mainTitle|会议室占用率} {subTitle|(工作日9:00-18:00)}',
      left: 'center',
      top: '2%',
      textStyle: {
        rich: {
          mainTitle: {
            fontSize: nowSize(20)
          },
          subTitle: {
            fontSize: nowSize(12),
            color: '#999'
          }
        }
      }
    },
    legend: {
      selectedMode: false,
      right: '4%',
      top: '7%',
      textStyle: {
        fontSize: nowSize(15)
      }
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
        }
      }
    },
    xAxis: {
      type: 'category',
      data: [],
      axisLabel: {
        show: true,
        interval: 0,
        rotate: -20,
        inside: false,
        margin: 11,
        textStyle: {
          fontSize: nowSize(15)
        }
      }
    },
    series: [],
    tooltip: {}
  };

  // x 轴
  option.xAxis.data = result.value.map((item: any) => item.roomName);

  const seriesItems = ['未占用', 'top1', 'top2', 'top3', 'others'];
  const series: any = seriesItems.map((name) => ({
    name,
    type: 'bar',
    stack: 'total',
    barWidth: '60%',
    label: name === '未占用' ? {show: false} : {
      show: true,
      formatter: percentageFormatter,
      textStyle: {fontSize: nowSize(15)}
    }, // 不展示 notOccupancy 的 label
    itemStyle: name === '未占用' ? {color: 'rgba(180, 180, 180, 0.2)'} : {}, // 修改 notOccupancy 的颜色
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

      return seriesInfo.marker + '时间段：' + timeRange + `<br>${seriesInfo.marker}` + '占用次数：' + times + `次<br>${seriesInfo.marker}` + '百分比：' + percentageFormatter({value: percentage});
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
  height: 600px;
}
</style>