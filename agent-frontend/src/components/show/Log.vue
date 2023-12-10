<script setup>
import {clearLogData, getLogData} from "@/utils/DataApi";
import {reactive, ref} from "vue";
import config from '@/utils/config'

const logList = reactive({data: []})
const socket = ref()
const setup = () => {
  getLogData().then(res => {
    if (res.data.msg !== '') {
      logList.data = res.data.msg.split("\r\n")
    }
  })
  socket.value = new WebSocket(config.GetLogWsLocation())
  socket.value.onmessage = (event) => {
    logList.data.push(event.data)
  }
}
const clearLog = () => {
  clearLogData()
  logList.data.length = 0
}
setup()
</script>

<template>
  <div>
    <el-button @click="clearLog" type="primary" style="margin-bottom: 10px">清空输出</el-button>
    <div v-if="logList.data.length === 0">
      <el-empty :image-size="300"/>
    </div>
    <div id="context" v-else>
      <div v-for="(str, index) in logList.data" :key="index" class="output">
        <span>{{ str }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
#context {
  max-height: 500px;
  overflow: scroll;
  text-align: left;
  background: gainsboro;
  border-radius: 10px;
  border: 1px solid gainsboro;
  box-shadow: 3px 3px gainsboro;
}
#context::-webkit-scrollbar{
  display: none;
}
.output {
}
</style>