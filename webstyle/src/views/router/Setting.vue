<script setup lang="ts">
import {mgExistTask, mgStartTask, mgStopTask} from "@/api/manageApi";
import {ref} from "vue";
import {useRouter} from "vue-router";

const router = useRouter()
const runTask = () => {
  mgStartTask().then(res => {
    if (res.isWant()) {
      status.value = true
      router.push('/router/log')
    }
  })
}
const stopTask = () => {
  mgStopTask().then(res => {
    if (res.isWant()) {
      router.push("/connect")
    }
  })
}
const status = ref(false)

mgExistTask().then(res => {
  // 其他
  if (res.isWant()) {
    status.value = true
  }
})
</script>

<template>
  <div>
    <el-card class="box-card">
      <span class="status">状态</span>
      <el-switch class="status_switch" v-model="status"/>
      <el-button type="primary" @click="runTask">调试</el-button>
      <el-button type="primary" @click="stopTask">停止调试</el-button>
    </el-card>
  </div>
</template>

<style scoped>
.status {
  margin-right: 10px;
}

.status_switch {
  margin-right: 10px;
}
</style>