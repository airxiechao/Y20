<template>
  <div class="man-machine-test">
    <q-card>
      <q-card-section>
        <div class="text-h6">人机检测
        </div>
      </q-card-section>
      <q-card-section class="q-pt-none">
        下面的图案是：
      </q-card-section>

      <q-card-section>
        <canvas ref="canvas" width="100" height="100"></canvas>
      </q-card-section>

      <q-separator />

      <q-card-actions vertical>
        <q-btn flat @click="onClickAnswer('LINE')">直线</q-btn>
        <q-btn flat @click="onClickAnswer('RECTANGLE')">矩形</q-btn>
        <q-btn flat @click="onClickAnswer('CIRCLE')">圆形</q-btn>
        <q-btn flat @click="onClickAnswer('TRIANGLE')">三角</q-btn>
      </q-card-actions>
      
    </q-card>
    
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import manMachineTestApi from '@/api/manMachineTest/manMachineTest.api'

export default {
  setup(props, { emit }) {
    const canvas = ref(null)
    const token = ref(null)
    const points = ref([])
    const flagFailed = ref(null)

    function drawPoint(ctx, x, y){
      ctx.fillStyle = "#fff"
      ctx.beginPath()
      ctx.arc(x, y, 3, 0, Math.PI * 2, true)
      ctx.fill()
    }

    function drawCanvas(points){
      const ctx = canvas.value.getContext('2d')

      ctx.fillStyle = 'green'
      ctx.fillRect(0, 0, 100, 100)

      points.forEach(p => {
        drawPoint(ctx, p.x, p.y)
      })
    }

    function drawFailed(){
      const ctx = canvas.value.getContext('2d')

      ctx.fillStyle = 'white'
      ctx.fillRect(0, 0, 25, 25)

      ctx.fillStyle = "red"
      ctx.font = '25px serif';
      ctx.fillText('×', 0, 22)
    }

    function drawPassed(){
      const ctx = canvas.value.getContext('2d')

      ctx.fillStyle = 'white'
      ctx.fillRect(0, 0, 25, 25)

      ctx.fillStyle = "green"
      ctx.font = '25px serif';
      ctx.fillText('√', 0, 22)
    }

    function nextQuestion(){
      manMachineTestApi.nextQuestion().then(resp => {
        token.value = resp.data.token
        points.value = resp.data.detail
      })
    }

    function checkAnswer(answer){
      manMachineTestApi.checkAnswer({
        token: token.value,
        answer: answer,
      }).then(resp => {
        // passed
        drawPassed()
        setTimeout(()=>{
          emit('passed', {
            token: token.value,
            answer: answer,
          })
        }, 100)
      }, reps => {
        // failed
        drawFailed()
        setTimeout(nextQuestion, 500)
      })
    }

    onMounted(() => {
      drawCanvas(points.value)
      nextQuestion()
    })

    watch(points, (newValue, oldValue)=>{
      drawCanvas(newValue)
    })

    return {
      canvas,

      onClickAnswer(answer){
        checkAnswer(answer)
      },
    }
  }
}
</script>

<style lang="scss" scoped>
.man-machine-test{
  width: 132px;
}
</style>