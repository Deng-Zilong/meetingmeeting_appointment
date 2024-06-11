<template>
  <div ref="chart" style="width: 600px; height:600px;"></div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts';// 5.4区别4引入方式

import { onMounted, ref, watch } from 'vue'
import { getRoomOccupancyDate } from '@/request/api/manage'

const chart = ref()
const chartInstance = ref()

let data = ref<any>([]) // 接收后端接口数据

const setChart = () => {
  if (!chartInstance) return
// 指定图表的配置项和数据
  const option = {
    title: {
      text: '统计七日内各会议室占用率（不包括周末9：00-18：00）',
      left: 'center'
    },
    legend: {  // 图例
      show: false,
      orient: 'vertical',
      right: 0,
      top: 30,
      bottom: 20,
      icon: 'circle', // 展示icon
      data: data.value.map((item: any) =>{
        return {
          name: item.name
        }
      }),
    },
    tooltip: { // 提示框浮层设置
      trigger: 'item',  // 触发类型，默认数据触发，可选为：'item' | 'axis'
      formatter: (params: any) => {
        let str = params.marker + '使用次数：' + params.value + '次' + '<br>' + params.marker + params.name + ' : ' + params.percent + '%';
        return str;
      }
    },
    xAxis: {
      show: false
    },
    yAxis: {
      show: false
    },
    series: [
      {
        type: 'pie',
        radius: '60%',  // 饼图大小 如果有两个属性值，如：['60%', '70%']，第一个值表示内圈大小，第二个表示外圈大小
        // center: ['50%','60%'],
        label: {
          show: true, // 是否显示文字
          backgroundColor: '#ECF0F3',
          formatter: (params: any) => { // 格式化文字
            return params.name + ' : ' + params.percent + '%';
          }
        },
        data: data.value.map((item: any) => {
          return {
            value: item.occupied,
            name: item.name
          }
        })
      }
    ]
  };

  chartInstance.value.setOption(option);  // 
};
// 使用刚指定的配置项和数据显示图表。
onMounted(() => {
  chartInstance.value = echarts.init(chart.value);  // 初始化图表
  
  getRoomOccupancyDate()  // 获取会议室占用率数据 接口
    .then((res) => {
      data.value = res.data
      setChart();  // 数据请求到后渲染图表 刷新chart
    })
    .catch((err) => {})
})

// 当roomX或occupied改变时，更新图表
watch([], () => setChart(), { deep: true });

</script>
