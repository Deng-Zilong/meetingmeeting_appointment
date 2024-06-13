/**
 * @description: 全局配置 方法
 *
 */

import { ref } from 'vue';

// 节流
export const useThrottle = (fn: Function, delay: number) => {
  const canRun = ref(true);
  return (...args: any[]) => {
    if (!canRun.value) return;
    canRun.value = false;
    setTimeout(() => {
      fn(...args);
      canRun.value = true;
    }, delay);
  };
};

// 防抖
export const useDebounce = (fn: Function, delay: number) => {
    let timer: ReturnType<typeof setTimeout>;
    return (...args: any[]) => {
      if (timer) clearTimeout(timer);
      timer = setTimeout(() => {
        fn(...args);
      }, delay);
    };
  };