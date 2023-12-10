<script setup>
import {useRouter} from "vue-router"
import {getContextNamespace, getFrameworkNamespace} from "@/utils/DataApi";
import {useStore} from "vuex";
import {inject, onUnmounted, reactive, ref} from "vue";
import {Message} from "@/utils/message";

const router = useRouter()
const store = new useStore()
const interval = ref()
const hasStack = ref(true)

const dynamicRouter = (key) => {
  router.push({path: '/info/' + key})
}
const stackRouter = () => {
  store.commit('CLEAN_FLAG', 'stack')
  hasStack.value = true
  router.push('/info/stack')
}
const logRouter = () => {
  router.push('/info/log')
}

const setup = () => {
  getContextNamespace().then(res => {
    for (let elem of res.data.msg) {
      middlewareList.list.add(elem)
    }
  })
  getFrameworkNamespace().then(res => {
    for (let elem of res.data.msg) {
      frameworkList.list.add(elem)
    }
  })
  interval.value = setInterval(() => {
    let currentPath = router.currentRoute._rawValue.fullPath
    if (store.state.stackFlag === true && !currentPath.endsWith('/info/stack')) {
      hasStack.value = false
    }
    if (store.state.contextFlag === true) {
      Message('上下文', '发现新的中间件上下文', 'success')
      store.commit('CLEAN_FLAG', 'context')
    }
    if (store.state.frameworkFlag === true) {
      Message('框架', '发现新的框架注册行为', 'success')
      store.commit('CLEAN_FLAG', 'framework')
    }
  }, 1000)
}
const middlewareList = reactive({list: new Set([])})
const frameworkList = reactive({list: new Set([])})
window.routersocket.context.onmessage((event) => {
  let context = JSON.parse(event.data)
  middlewareList.list.add(context.from)
})
window.routersocket.framework.onmessage((event) => {
  let framework = JSON.parse(event.data)
  frameworkList.list.add(framework.from)
})
onUnmounted(() => {
  clearInterval(interval.value)
})
setup()
</script>

<template>
  <div>
    <h5>菜单</h5>
    <el-menu
        class="list_menu">
      <el-sub-menu index="1">
        <template #title>
          <img src="@/assets/middleware.png" alt="middleware" class="nav_img"/>
          <span>中间件</span>
        </template>
        <el-menu class="el-menu-demo" v-for="item in middlewareList.list" @select="dynamicRouter">
          <el-menu-item :index="item">
            <img :src="require('../../assets/' + item + '.png')" class="nav_img" :alt="item"/>
            <span>{{ item }}</span>
          </el-menu-item>
        </el-menu>
      </el-sub-menu>
      <el-sub-menu index="2">
        <template #title>
          <img src="@/assets/framework.png" alt="framework" class="nav_img"/>
          <span>框架</span>
        </template>
        <el-menu class="el-menu-demo" v-for="item in frameworkList.list" @select="dynamicRouter">
          <el-menu-item :index="item">
            <img :src="require('../../assets/' + item + '.png')" class="nav_img" :alt="item"/>
            <span>{{ item }}</span>
          </el-menu-item>
        </el-menu>
      </el-sub-menu>

      <el-menu-item @click="stackRouter">
        <template #title>
          <img src="@/assets/stack.png" alt="stack" class="nav_img">
          <span><el-badge :hidden="hasStack" value="new" class="item">堆栈</el-badge></span>
        </template>
      </el-menu-item>

      <el-menu-item @click="logRouter">
        <template #title>
          <img src="@/assets/log.png" alt="log" class="nav_img">
          <span>日志</span>
        </template>
      </el-menu-item>
    </el-menu>
  </div>
</template>
<style scoped>
.nav_img {
  width: 24px;
  height: 24px;
  margin-right: 20px;
}

.el-menu {
  border-radius: 10px;
}
</style>