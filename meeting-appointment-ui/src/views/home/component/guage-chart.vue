<template>
  <div ref="chart" :style="`width: ${chartWidth}; height: ${chartHeight};`"></div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
// 使用 ECharts 提供的按需引入的接口来打包必须的组件
// 引入 echarts 核心模块，核心模块提供了 echarts 使用必须要的接口
import * as echarts from 'echarts/core'
// 引入仪表盘图表，图表后缀都为 Chart
import { GaugeChart } from 'echarts/charts'
// 引入提示框，组件后缀都为 Component
import { TooltipComponent } from 'echarts/components'
// 引入 Canvas 渲染器，注意引入 CanvasRenderer 或者 SVGRenderer 是必须的一步
import { CanvasRenderer } from 'echarts/renderers'
// 注册必须的组件
echarts.use([GaugeChart, TooltipComponent, CanvasRenderer])

const chart = ref()
const gaugeChart = ref()
const gradient = ref({ // 自定义渐变色
  type: 'linear',
  x: 0,
  y: 0,
  x2: 0,
  y2: 1,
  colorStops: [
    {
      offset: 0, color: '#FF6E76' // 0% 处的颜色
    },
    {
      offset: 0.25, color: '#FDDD60' // 25% 处的颜色
    },
    {
      offset: 0.75, color: '#58D9F9' // 75% 处的颜色
    },
    {
      offset: 1, color: '#7CFFB2' // 100% 处的颜色
    }
  ],
  global: false // 缺省为 false
})

interface Gauge {
  name: string // 数据项名称
  value: number // 数据值
  [propName: string]: any // 添加一个字符串索引签名，用于包含带有任意数量的其他属性
}
interface Props {
  gaugeData: Gauge[] // 仪表盘数据源
  width?: string|number // 容器宽度
  height?: string|number // 容器高度
}
const props = withDefaults(defineProps<Props>(), {
  gaugeData: () => [],
  width: '100%',
  height: '100%'
})
const chartWidth = computed(() => {
  if (typeof props.width === 'number') {
    return props.width + 'px'
  } else {
    return props.width
  }
})
const chartHeight = computed(() => {
  if (typeof props.height === 'number') {
    return props.height + 'px'
  } else {
    return props.height
  }
})

// 刷新 chart 渲染
const refreshChart = () => {
  if (!gaugeChart.value) return
  const option = {
    tooltip: { // 提示框浮层设置
      trigger: 'item',
      triggerOn: 'mousemove', // 提示框触发条件
      enterable: true, // 鼠标是否可进入提示框浮层中，默认false
      confine: true, // 是否将tooltip框限制在图表的区域内
      formatter: function (params: any) { // 提示框浮层内容格式器，支持字符串模板和回调函数两种形式
        return params.marker + params.name + ': ' + params.value || '--'
      },
      backgroundColor: 'transparent', 
      borderColor: '#719BE8', 
      borderWidth: 1, 
      borderRadius: 6, 
      padding: [6, 12], 
      textStyle: { 
        color: '#333', 
        fontWeight: 600,
        fontSize: 18, 
        // 文字超出宽度是否截断或者换行；只有配置width时有效
        overflow: 'breakAll', // truncate截断，并在末尾显示ellipsis配置的文本，默认为...;break换行;breakAll换行，并强制单词内换行
        ellipsis: '...'
      },
    },
    color: ['#719BE8'],
    series: [
      {
        type: 'gauge',
        name: '仪表盘', 
        colorBy: 'data',
        center: ['50%', '58%'], 
        radius: '85%', // 仪表盘半径
        legendHoverLink: true, 
        startAngle: 220, // 仪表盘起始角度
        endAngle: -40, // 仪表盘结束角度
        clockwise: true, 
        min: 0, // 最小的数据值，映射到 minAngle
        max: 100, // 最大的数据值，映射到 maxAngle
        splitNumber: 10, // 仪表盘刻度的分割段数
        axisLine: { 
          show: true, // 是否显示仪表盘轴线
          roundCap: true, // 是否在两端显示成圆形
          lineStyle: { // 仪表盘轴线样式
            width: 30, // 轴线宽度
            color: [ // 仪表盘的轴线可以被分成不同颜色的多段。每段的结束位置和颜色可以通过一个数组来表示
              // [20, '#FF6E76'],
              // [40, '#FDDD60'],
              // [60, '#58D9F9'],
              // [80, '#7CFFB2'],
              [100, '#468EFD']
            ]
          }
        },
        progress: { // 展示当前进度
          show: true, // 是否显示进度条
          overlap: false, // 多组数据时进度条是否重叠
          roundCap: true, // 是否在两端显示成圆形
          clip: true, // 是否裁掉超出部分
          itemStyle: { // 进度条样式
            color: gradient.value, // 图形的颜色
          }
        },
        splitLine: { // 分隔线样式
          show: true, 
          length: 20, 
          distance: -65, 
          lineStyle: { 
            color: '#468EFD',
            width: 3, 
            type: 'solid', // 线的类型，可选 'solid' 'dashed' 'dotted'
            cap: 'round' // 指定线段末端的绘制方式，可选 'butt'(默认) 线段末端以方形结束 'round' 圆形 'square' 方形，但是增加了一个宽度和线段相同，高度是线段厚度一半的矩形区域
          }
        },
        axisTick: { // 刻度样式
          show: true,
          splitNumber: 5,
          length: 12,
          distance: -50,
          lineStyle: { 
            color: '#468EFD',
            width: 3, 
            type: 'solid',
            cap: 'round' 
          }
        },
        axisLabel: { // 刻度标签
          show: false, // 是否显示标签
        },
        pointer: { // 仪表盘指针
          show: false
        },
        title: { // 仪表盘标题
          show: true,
          offsetCenter: [0, '18%'], 
          color: '#1273DB', 
          fontStyle: 'normal', // 文字字体的风格，可选 'normal' 'italic' 'oblique'
          fontWeight: 'bold',
          fontFamily: 'sans-serif', // 文字的字体系列，可选 'serif' , 'monospace', 'Arial', 'Courier New', 'Microsoft YaHei', ...
          fontSize: 20
        },
        detail: { // 仪表盘详情，用于显示数据，即表盘中心的数据展示
          show: true, 
          color: '#1677FF', 
          fontStyle: 'normal',
          fontWeight: 'bold',
          fontFamily: 'Microsoft YaHei',
          fontSize: 50, 
          backgroundColor: 'transparent',
          valueAnimation: true, // 是否开启标签的数字动画
          offsetCenter: [0, '-20%'],
          formatter: function (value: number) { // 格式化函数或者字符串
            return value + ''
          },
        },
        /*
        对象支持的所有属性：{ title , detail , name , value , itemStyle }
        */
        data: props.gaugeData
      }
    ]
  }
  gaugeChart.value.setOption(option)
}

// 监听 gaugeData 变化刷新 chart 渲染
watch(() => props.gaugeData, () => refreshChart(), { deep: true })

onMounted(() => {
  // 初始化 chart
  gaugeChart.value = echarts.init(chart.value)
  // 刷新chart
  refreshChart()
})
</script>
