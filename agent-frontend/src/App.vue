<script setup>
import {useStore} from "vuex";
import RouterSocket from "@/utils/RouterSocket";
import config from "@/utils/config";
import {nextTick, provide, ref} from "vue";

const store = new useStore()
window.routersocket = {
  stack: new RouterSocket(config.GetStackWsLocation(), 'stack', store),
  framework: new RouterSocket(config.GetFrameworkWsLocation(), 'framework', store),
  context: new RouterSocket(config.GetContextWsLocation(), 'context', store)
}

const isRouterAlive = ref(true)


const reload = () => {
  isRouterAlive.value = false
  nextTick(function () {
    isRouterAlive.value = true
  })
}
provide('reload', reload)
</script>
<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<style>
#app {
  text-align: center;
}

body {
  background:#f4f5f6;
}
</style>
