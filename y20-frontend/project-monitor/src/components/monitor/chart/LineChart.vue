<template>
  <div class="line-chart" ref="el"></div>
</template>

<style lang="scss">
.line-chart{
  width: 100%;
  height: 200px;
}
</style>

<script>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import * as echarts from "echarts"

export default {
  props: ['title', 'data'],
  setup(props){
    const el = ref(null)
    const chart = ref(null)

    const initChart = (title, data, el) => {
      const chart = echarts.init(el);
      chart.setOption({
        title: {
          left: 'center',
          text: title
        },
        grid: {
          left: 40,
          top: 50,
          right: 20,
          bottom: 50
        },
        xAxis: {
          type: "time",
          axisLabel: {
            formatter: {
              day: '{MM}/{dd}',
              hour: '{HH}:{mm}'
            }
          }
        },
        yAxis: {
          type: "value"
        },
        dataZoom: [
          {
            type: 'inside'
          }
        ],
        series: [
          {
            type: "line",
            smooth: true,
            symbol: 'none',
            data: data
          }
        ]
      });

      new ResizeObserver(entries => {
        chart.resize();
      }).observe(el);
      
      return chart;
    }

    const updateChart = (chart, data) => {
      let option = chart.getOption();
      option.series[0].data = data;
      chart.setOption(option);    
    }

    watch(() => props.data, (newValue, oldValue) => {
      updateChart(chart.value, newValue)
    })

    onMounted(() => {
      chart.value = initChart(props.title, props.data, el.value)
    })

    onUnmounted(() => {
      if(chart.value){
        chart.value.dispose();
      }
    })

    return {
      el,
    }
  }
}
</script>