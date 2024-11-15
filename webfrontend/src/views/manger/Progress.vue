<template>
  <div v-if="progresses.length === 0">
    <el-empty description="无任务" />
  </div>
  <el-scrollbar height="700px" v-else>
    <progress-item v-for="progress in progresses" :progress="progress" />
  </el-scrollbar>
</template>
<script setup lang="ts">

import ProgressItem from "@/components/manager/ProgressItem.vue";
import {apiGetProgress, Progress} from "@/api/progress";
import {ref} from "vue";
import {GetBaseUrl} from "@/config";

const progresses = ref<Progress[]>([])
apiGetProgress().then(res => {
  progresses.value = res
})

const webSocket = new WebSocket("ws://" + GetBaseUrl() + "/ws/event");
webSocket.onmessage = channel => {
  let progress = new Progress(JSON.parse(channel.data))
  let exist: boolean = false
  progresses.value.map(item => {
    if (item.id === progress.id) {
      exist = true
      item.percentage = progress.percentage
      item.message = progress.message
    }
  })
  if (!exist) {
    progresses.value.push(progress)
  }
}
</script>
<style scoped>
</style>
