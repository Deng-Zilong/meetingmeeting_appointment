<template>
  <div ref="echartsDom" style="width: 770px; height:600px;"></div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'; // 5.4区别4引入方式
import {onMounted, ref, watch} from 'vue'
import { getRoomOccupancyDate } from '@/request/api/manage'

const echartsDom = ref()
const echartsInstance = ref()
const props = defineProps({
  barData: Array
})
let result = ref<any>() // 接收后端接口数据

const option: any = {
  legend: { selectedMode: false  },
  grid: {},
  yAxis: { type: 'value', data: [] },
  xAxis: {
    type: 'category', data: [],
    axisLabel: {
      show: true, // 是否显示刻度标签，默认显示
      interval: 0, // 坐标轴刻度标签的显示间隔，在类目轴中有效；默认会采用标签不重叠的策略间隔显示标签；可以设置成0强制显示所有标签；
      rotate: -20, // 刻度标签旋转的角度，在类目轴的类目标签显示不下的时候可以通过旋转防止标签之间重叠；旋转的角度从-90度到90度
      inside: false, // 刻度标签是否朝内，默认朝外
      margin: 11, // 刻度标签与轴线之间的距离
    },
  },
  series: [],
  tooltip: {}
};

const percentageFormatter = (params: any) => Math.round(params.value * 1000) / 1000 + '%'

const refreshChart = () => {

  // x 轴
  const xAxis = result.value.map((item: any) => item.roomName)
  option.xAxis.data = xAxis
  
  // const yAxis = result.value.map((item: any) => item.totalOccupancy)
  // option.yAxis.data = yAxis
  // result.data 每项是y轴的一个数据
  const seriesItems = ['top1', 'top2', 'top3', 'other', '未占用']
  const series: any = seriesItems.map((name, sid) => ({
    name,
    type: 'bar',
    stack: 'total',
    barWidth: '60%',
    label: { show: true, formatter: percentageFormatter },
    data: []
  }))
  result.value.forEach((item: any) => {
    item.timePeriods.forEach((timeItem: { occupancyRate: number }, index: number) => {
      series[index].data.push(timeItem.occupancyRate)
    })
    series[series.length - 1].data.push((item.total - item.totalOccupancy) / item.total)
  })
  option.series = series.reverse()  // 倒序
  option.tooltip = {
    formatter: (seriesInfo: any) => {
      const xAxisIndex = seriesInfo.dataIndex // x 轴索引
      const seriesIndex = seriesInfo.seriesIndex // 维度索引

      const seriesItem = result.value[xAxisIndex] // 维度数据
      const timeRange = seriesItem.timePeriods?.[seriesIndex]?.timePeriod || '未占用' // 时间段
      const times = seriesItem.timePeriods?.[seriesIndex]?.occupied || seriesItem.total - seriesItem.totalOccupancy
      const percentage = seriesItem.timePeriods?.[seriesIndex]?.occupancyRate || (seriesItem.total - seriesItem.totalOccupancy) / seriesItem.total
      return `<ul style="padding-left: 20px">
        <li>时间段：${timeRange}</li>
        <li>次数：${times}</li>
        <li>百分比：${percentageFormatter({ value: percentage })}</li>
      </ul>`
      // let str = seriesInfo.marker + timeRange + '<br>' + '次数：' + times + '<br>' + '百分比：' + percentageFormatter({ value: percentage })
      // return str;
    }
  }

  echartsInstance.value.setOption(option)
}

onMounted(async() => {
  echartsInstance.value = echarts.init(echartsDom.value)
  result.value = (await getRoomOccupancyDate({})).data
  refreshChart()
})

// 当roomX或occupied或total改变时，更新图表
watch(() => [props.barData,result.value], () => {
  refreshChart()
  if (props.barData) {
    result.value = props.barData
    console.log(props.barData,'监听props子',result.value)
  }
}, { deep: true });

</script>
