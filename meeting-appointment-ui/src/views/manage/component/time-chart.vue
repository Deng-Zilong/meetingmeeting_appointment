<template>
  <div ref="chart" style="width: 770px;height:637px;"></div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts';// 5.4区别4引入方式

import { onMounted, ref, watch } from 'vue'
import { getTimePeriodDate } from '@/request/api/manage'
import { dayjs } from 'element-plus';

const chart = ref()
const chartInstance = ref()

let timeX = ref<any>([])  // x轴数据
let chartData = ref<any>([])  // y轴数据

const setChart = () => {
  if (!chartInstance) return
// 指定图表的配置项和数据
  const option = {
    title: {
      text: '近五个工作日使用频次前十的时间段',
      top: "5%",
      left: 'center',
    },
    legend: {
      data: ["时间段使用总次数"],
      top: "10%"
    },
    grid: {
      top: '17%'
      // top: '100px',
      // left: '50px',  // grid布局设置适当调整避免X轴文字只能部分显示
      // right: '100px', // grid布局设置适当调整避免X轴文字只能部分显示
      // bottom: '70px',
    },
    tooltip: { // 提示框浮层设置 删除就不显示了
    },
    // color: ['#719BE8'],  // 柱状图颜色
    xAxis: {
      data: [],
        axisLabel: {
          show: true, // 是否显示刻度标签，默认显示
          interval: 0, // 坐标轴刻度标签的显示间隔，在类目轴中有效；默认会采用标签不重叠的策略间隔显示标签；可以设置成0强制显示所有标签；
          rotate: -20, // 刻度标签旋转的角度，在类目轴的类目标签显示不下的时候可以通过旋转防止标签之间重叠；旋转的角度从-90度到90度
          inside: false, // 刻度标签是否朝内，默认朝外
          margin: 11, // 刻度标签与轴线之间的距离
          // formatter: '{value}' , // 刻度标签的内容格式器
        }, 
    },
    yAxis: {},
    series: [
      {
        type: 'bar',
        name: "时间段使用总次数", // legend属性
        barWidth: 20,
        // barGap:'80%',/*多个并排柱子设置柱子之间的间距*/
  	    // barCategoryGap:'50%',/*多个并排柱子设置柱子之间的间距*/
        data: [],
        label: {
          show: true,
        }
      },      
    ]
  };

  // 更新图表的option
  option.xAxis.data = timeX.value.slice(0, 10);
  option.series[0].data = chartData.value.slice(0, 10);

  chartInstance.value.setOption(option);  // 
};
// 使用刚指定的配置项和数据显示图表。
onMounted(() => {
  chartInstance.value = echarts.init(chart.value);  // 初始化图表

  setChart();  // 刷新chart

  getTimePeriodDate()  // 获取时间段数据 接口
    .then((res) => {
      res.data.map((item: any) => {
        timeX.value.push(`${dayjs(item.startTime).format('HH:mm')}-${dayjs(item.endTime).format('HH:mm')}`)
        chartData.value.push(item.count)
      })
    })
    .catch((err) => {})
})

// 当timeX或chartData改变时，更新图表
watch([timeX, chartData], () => setChart(), { deep: true });

</script>
