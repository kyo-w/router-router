<script setup lang="ts">
import {useRouter} from "vue-router";
import {getCurrentInstance, reactive, ref} from "vue";
import {createDiscoveryWs, createFailServletWs} from "@/utils/routerService";
import {getContextType, getFrameworkType} from "@/api/dataApi";

const router = useRouter()
const middlewareList = reactive({list: new Set<any>([])})
const frameworkList = reactive({list: new Set<any>([])})
const getTheme = getCurrentInstance().appContext.config.globalProperties.getTheme
const alias: any = {
  TOMCAT: 'tomcat',
  JETTY_WEB: 'jetty',
  STRUTS_1: 'struts',
  STRUTS_2: 'struts',
  JERSEY_1: 'jersey',
  JERSEY_2: 'jersey',
  SPRING: 'spring'
}
const hasFramework = ref(true)
const hasContext = ref(true)
const servletFail = ref(true)

const contextDynamicRouter = (key: string) => {
  hasContext.value = true
  router.push({path: '/router/' + key})
}
const frameworkDynamicRouter = (key: string) => {
  hasFramework.value = true
  router.push({path: '/router/' + key})
}


const setRouter = () => {
  router.push({path: '/router/setting'})
}
const logRouter = () => {
  router.push({path: '/router/log'})
}
const stackRouter = () => {
  router.push({path: '/router/stack'})
}
createDiscoveryWs((event) => {
  let {name, type} = JSON.parse(event.data)
  if (type === 'middle') {
    hasContext.value = false
    middlewareList.list.add(alias[name])
  }
  if (type === 'framework') {
    hasFramework.value = false
    frameworkList.list.add(alias[name])
  }
})
getContextType().then(res => {
  for (let contextType of res.getContext()) {
    middlewareList.list.add(alias[contextType])
  }
})
createFailServletWs(event => {
  servletFail.value = false
})
getFrameworkType().then(res => {
  for (let frameworkType of res.getContext()) {
    frameworkList.list.add(alias[frameworkType])
  }
})
</script>

<template>
  <div>
    <img src=""/>
    <el-menu
        class="list_menu">
      <el-menu-item @click="setRouter" index="1">
        <template #title>
          <img :src="require('../../assets/setting' + getTheme() + '.png')" alt="setting" class="nav_img"/>
          <span>调试</span>
        </template>
      </el-menu-item>
      <el-menu-item @click="logRouter" index="2">
        <template #title>
          <img :src="require('../../assets/log' + getTheme() + '.png')" alt="log" class="nav_img"/>
          <span>调试日志</span>
        </template>
      </el-menu-item>
      <el-sub-menu index="3">
        <template #title>
          <img :src="require('../../assets/middleware' + getTheme() + '.png')" alt="middleware" class="nav_img"/>
          <span><el-badge :hidden="hasContext" value="new" class="item">中间件</el-badge></span>
        </template>
        <el-menu class="el-menu-demo" v-for="item in middlewareList.list" @select="contextDynamicRouter">
          <el-menu-item :index="item">
            <img :src="require('../../assets/' + item + getTheme() +  '.png')" class="nav_img" :alt="item"/>
            <span>{{ item }}</span>
          </el-menu-item>
        </el-menu>
      </el-sub-menu>
      <el-sub-menu index="4">
        <template #title>
          <img :src="require('../../assets/framework' + getTheme() + '.png')" alt="framework" class="nav_img"/>
          <span><el-badge :hidden="hasFramework" value="new" class="item">框架</el-badge></span>
        </template>
        <el-menu class="el-menu-demo" v-for="item in frameworkList.list" @select="frameworkDynamicRouter">
          <el-menu-item :index="item">
            <img :src="require('../../assets/' + item + getTheme() +  '.png')" class="nav_img" :alt="item"/>
            <span>{{ item }}</span>
          </el-menu-item>
        </el-menu>
      </el-sub-menu>
      <el-menu-item @click="servletFail = true;router.push('/router/servlet')" index="5">
        <template #title>
          <img :src="require('../../assets/servlet' + getTheme() + '.png')" alt="setting" class="nav_img"/>
          <span><el-badge :hidden="servletFail" value="new" class="item">Servlet失败记录</el-badge></span>
        </template>
      </el-menu-item>

      <!--      <el-menu-item @click="stackRouter">-->
      <!--        <template #title>-->
      <!--          <img :src="require('../../assets/stack' + getTheme() + '.png')" alt="stack" class="nav_img"/>-->
      <!--          <span><el-badge :hidden="hasStack" value="new" class="item">堆栈</el-badge></span>-->
      <!--        </template>-->
      <!--      </el-menu-item>-->
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