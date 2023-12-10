<script setup>

import FrameworkHeader from "@/components/show/context/FrameworkHeader.vue";
import {reactive, ref} from "vue";
import {getFrameworkByName} from "@/utils/DataApi";


const props = defineProps({
  name: {
    type: String,
    required: true
  }
})
const framework = reactive({list: []})
const setup = () => {
  getFrameworkByName(props.name).then(res => {
    for (let elem of res.data.msg) {
      framework.list.push(elem)
    }
  })
}
const activeName = ref(props.name + '-0')
const handlerClick = (key) => {
  activeName.value = key.props.name
}

setup()
</script>

<template>
  <el-tabs v-model="activeName" @tab-click="handlerClick" type="border-card">
    <el-tab-pane :label="elem.from + '-' + key" :name="elem.from + '-' + key" v-for="(elem, key) in framework.list">
      <el-container class="container">
        <el-header class="header">
          <FrameworkHeader :framework="elem"/>
        </el-header>
        <el-main class="context">
          <slot name="tableList" :data="elem"></slot>
        </el-main>
      </el-container>
    </el-tab-pane>
  </el-tabs>
</template>

<style scoped>
.container {
  height: 100%;
}

.header {
  height: auto;
}

.context {
  flex: 1;
}
</style>