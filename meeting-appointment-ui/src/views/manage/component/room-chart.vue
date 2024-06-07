<template>
  <div ref="chart" style="width: 770px; height:637px;"></div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'; // 5.4区别4引入方式
import {onMounted, ref, watch} from 'vue'
import {getRoomOccupancyDate} from '@/request/api/manage'

const chart = ref()
const chartInstance = ref()

let roomX = ref<any>([])  // x轴数据
let occupied = ref<any>([])  // y轴数据 已占用数
let total = ref<any>([])  // y轴数据 时间段可使用总次数

const setChart = () => {
  if (!chartInstance) return
// 指定图表的配置项和数据
  const option = {
    title: {
      text: '近五个工作日会议室占用率（ 9：00 - 18：00 ）',
      top: "5%",
      left: 'center',
    },
    legend: {
      data: ["时间段已占用个数", "时间段总数"],
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
        // formatter: (value: any) => {
        //   //x轴的文字超出 显示为...
        //   var str = value.split("");
        //   return str.slice(0, 15).join("") +  (str.length > 15 ? '...' : '');
        // },
      }, 
    },
    yAxis: {},
    series: [
      {
        type: 'bar',
        name: '时间段已占用个数',
        // barWidth: 20,
        // barGap:'0%',/*多个并排柱子设置柱子之间的间距*/
        // barCategoryGap:'50%',/*多个并排柱子设置柱子之间的间距*/
        color: '#59AFF4',
        data: [],
        label: {
          show: true,
        }
      },
      {
        type: 'bar',
        name: '时间段总数',
        // barWidth: 20,
        barGap: '-100%',
        z: '-1',
        color: '#D6ECFF',
        data: [],
        label: {
          show: true,
        }
      }
    ]
  };

  // 更新图表的option
  option.xAxis.data = roomX.value;
  option.series[0].data = occupied.value;
  option.series[1].data = total.value;

  chartInstance.value.setOption(option);  // 
};
// 使用刚指定的配置项和数据显示图表。
onMounted(() => {
  chartInstance.value = echarts.init(chart.value);  // 初始化图表

  setChart(); 

  getRoomOccupancyDate()  // 获取会议室占用率数据 接口
    .then((res) => {
      res.data.map((item: any) => {
        roomX.value.push(item.name)
        occupied.value.push(item.occupied)
        total.value.push(item.total)
      })
    })
    .catch((err) => {})
})

// 当roomX或occupied或total改变时，更新图表
watch(() => [roomX, occupied, total], () => setChart(), { deep: true });

</script>
