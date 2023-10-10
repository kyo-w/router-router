<template>
  <div id="app">
    <router-view v-if="isRouterAlive"/>
  </div>
</template>
<script setup>
import {nextTick, provide, ref} from "vue";
const isRouterAlive = ref(true)


const reload = () => {
  isRouterAlive.value = false
  nextTick(function () {
    isRouterAlive.value = true
  })
}
provide('reload', reload)


const debounce = (fn, delay) => {
  let timer = null;
  return function () {
    let context = this;
    let args = arguments;
    clearTimeout(timer);
    timer = setTimeout(function () {
      fn.apply(context, args);
    }, delay);
  }
}
const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
  constructor(callback) {
    callback = debounce(callback, 16);
    super(callback);
  }
}
</script>
<style>
html, body {
  background-color: #F2F6FC;
}
</style>
