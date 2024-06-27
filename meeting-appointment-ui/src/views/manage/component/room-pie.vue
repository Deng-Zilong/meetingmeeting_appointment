<template>
  <div ref="chart" class="room-pie"></div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'; // 5.4区别4引入方式
import {onBeforeUnmount, onMounted, ref, watch} from 'vue'
import {getRoomSelectionRate} from '@/request/api/manage'

const chart = ref()
const chartInstance = ref()
const props = defineProps({
  pieData: Array
})
let data = ref<any>([]) // 接收后端接口数据

const setChart = () => {
  if (!chartInstance) return

  // 指定图表的配置项和数据
  const option = {
    title: {
      text: '{mainTitle|统计会议室选择率} {subTitle|(工作日9:00-18:00)}',
      top: "2%",
      left: 'center',
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
    legend: {  // 图例
      show: true,
      orient: 'vertical',
      right: 0,
      top: 30,
      bottom: 20,
      icon: 'circle', // 展示icon
      textStyle: {
        fontSize: nowSize(14)
      },
      data: data.value.map((item: any) => {
        return {
          name: item.roomName
        }
      }),
    },
    tooltip: { // 提示框浮层设置
      trigger: 'item',  // 触发类型，默认数据触发，可选为：'item' | 'axis'
      formatter: (params: any) => {
        return params.marker + '使用次数：' + params.value + '次' + '<br>' + params.marker + params.name + ' : ' + params.percent + '%';
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
        center: ['50%', '60%'],
        label: {
          show: true, // 是否显示文字
          backgroundColor: '#ECF0F3',
          formatter: (params: any) => { // 格式化文字
            return params.name + ' : ' + params.percent + '%';
          },
          textStyle: {
            fontSize: nowSize(14),
            backgroundColor: '#FFF'
          },
        },
        data: data.value.map((item: any) => {
          return {
            value: item.selected,
            name: item.roomName
          }
        })
      }
    ]
  };

  chartInstance.value.setOption(option);
};

const nowSize = (val?: any, initWidth = 1920) => {
  return val / initWidth * document.documentElement.clientWidth;
}

window.addEventListener("resize", () => {
  chartInstance.value.resize();
  setChart();
});

onBeforeUnmount(() => {
  // 离开页面必须进行移除，否则会造成内存泄漏，导致卡顿
  window.removeEventListener("resize", setChart);
})

// 使用刚指定的配置项和数据显示图表。
onMounted(() => {
  chartInstance.value = echarts.init(chart.value);  // 初始化图表

  getRoomSelectionRate({})  // 获取会议室选择率数据 接口
      .then((res) => {
        data.value = res.data
        setChart();  // 数据请求到后渲染图表 刷新chart
      })
      .catch(() => {
      })

})

// 当data改变时，更新图表
watch(() => [data, props.pieData], () => {
  setChart()
  if (props.pieData) {
    data.value = props.pieData
  }
}, {deep: true});

</script>

<style lang="scss" scoped>
.room-pie {
  width: 770px;
  height: 600px;
}
</style>
