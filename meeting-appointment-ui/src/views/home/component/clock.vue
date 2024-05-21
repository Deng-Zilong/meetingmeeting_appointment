<template>
  <svg class="clock-svg" viewBox="-100 -100 200 200">
    <circle cx="0" cy="0" r="90" stroke="#fff" stroke-width="5" fill="none" />
    <!-- 小时刻度 -->
    <template v-for="i in 12" :key="i">

      <!-- 12点 刻度线 -->
      <line v-if="i === 12" :x1="0" :y1="-60" :x2="90 * Math.cos((i - 3) * (Math.PI / 6))"
        :y2="80 * Math.sin((i - 3) * (Math.PI / 6))" stroke="#fff" :stroke-width="2" />
      <!-- 3点 刻度线 -->
      <line v-if="i === 3" :x1="60" :y1="0" :x2="80 * Math.cos((i - 3) * (Math.PI / 6))"
        :y2="90 * Math.sin((i - 3) * (Math.PI / 6))" stroke="#fff" :stroke-width="2" />
      <!-- 6点 刻度线 -->
      <line v-if="i === 6" :x1="0" :y1="60" :x2="90 * Math.cos((i - 3) * (Math.PI / 6))"
        :y2="80 * Math.sin((i - 3) * (Math.PI / 6))" stroke="#fff" :stroke-width="2" />
      <!-- 9点 刻度线 -->
      <line v-if="i === 9" :x1="-60" :y1="0" :x2="80 * Math.cos((i - 3) * (Math.PI / 6))"
        :y2="90 * Math.sin((i - 3) * (Math.PI / 6))" stroke="#fff" :stroke-width="2" />

    </template>
    <!-- 时针 -->
    <line class="hand hour-hand" :x1="0" :y1="0" :x2="45 * Math.cos(currentHourRad)" :y2="45 * Math.sin(currentHourRad)"
      stroke="#fff" stroke-width="4" />
    <!-- 分针 -->
    <line class="hand minute-hand" :x1="0" :y1="0" :x2="65 * Math.cos(currentMinuteRad)"
      :y2="65 * Math.sin(currentMinuteRad)" stroke="#fff" stroke-width="3" />
    <!-- 秒针 -->
    <line class="hand second-hand" :x1="0" :y1="0" :x2="80 * Math.cos(currentSecondRad)"
      :y2="80 * Math.sin(currentSecondRad)" stroke="#3E78F4" stroke-width="1" />
  </svg>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, watchEffect } from 'vue';

export default defineComponent({
  setup() {
    const now = ref<Date>(new Date());
    let currentHourRad = ref(0);
    let currentMinuteRad = ref(0);
    let currentSecondRad = ref(0);

    const updateTime = () => {
      now.value = new Date();
      // currentHourRad.value = ((now.value.getHours() % 12) * (Math.PI / 6)) + (now.value.getMinutes() * (Math.PI / 360));
      // currentMinuteRad.value = (now.value.getMinutes() * (Math.PI / 30)) + (now.value.getSeconds() * (Math.PI / 1800));
      // currentSecondRad.value = (now.value.getSeconds() * (Math.PI / 30));
      currentHourRad.value = (now.value.getHours() % 12 + now.value.getMinutes() / 60) * (Math.PI / 6) - Math.PI / 2
      currentMinuteRad.value = (now.value.getMinutes() + now.value.getSeconds() / 60) * (Math.PI / 30) - Math.PI / 2
      currentSecondRad.value = (now.value.getSeconds() + now.value.getMilliseconds() / 1000) * (Math.PI / 30) - Math.PI / 2
    };

    watchEffect(() => {
      setInterval(updateTime, 1000);
    });

    onMounted(() => {
      updateTime();
    });

    return {
      currentHourRad,
      currentMinuteRad,
      currentSecondRad,
    };
  },
});
</script>

<style scoped>
.clock-svg {
  width: 200px;
  height: 200px;
}

.hand {
  transition: transform 0.05s;
}
</style>