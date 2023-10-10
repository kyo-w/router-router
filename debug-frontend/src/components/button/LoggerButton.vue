<template>
  <div class="logger_button">
    <el-button type="primary" @click="openLogger">连接日志</el-button>
  </div>
  <el-drawer
      v-model="props.logger"
      size="40%"
      title="连接日志"
      :before-close="handleClose"
  >
    <div v-if="logInfo.log.length > 0">
      <el-button type="primary" @click="cleanLog">清空日志</el-button>
    </div>
    <div class="log_content_box">
      <p v-for="item in logInfo.log" :class="['log_content', item.debugType]">
        {{ "[" + item.debugType + "]: " + item.debugMessage }}</p>
    </div>
  </el-drawer>
</template>
<script setup>
import {onBeforeUnmount, onMounted, reactive, ref} from "vue";
import config from "@/config";

const props = defineProps({
  logger: {type: Boolean, required: true}
})
const emit = defineEmits(['update:logger'])

let socket = ref()
let logInfo = reactive({log: []}
)


const openLogger = () => {
  emit('update:logger', true)
}
const handleClose = () => {
  emit('update:logger', false)
}
const cleanLog = () => {
  logInfo.log = []
  localStorage.removeItem('log-cache')
}
onMounted(() => {
  console.log(config.GetDebugWsLocation())
  socket.value = new WebSocket(config.GetDebugWsLocation())
  socket.value.onmessage = (event) => {
    logInfo.log.push(JSON.parse(event.data))
    localStorage.setItem('log-cache', JSON.stringify(logInfo.log))
  }
})
onBeforeUnmount(() => {
  socket.value.close()
})

const onCreate = () => {
  let logCache = JSON.parse(localStorage.getItem("log-cache"))
  if (Array.isArray(logCache)) {
    logInfo.log = logCache
  }
}
onCreate()
</script>

<style scoped>
.logger_button {
  display: inline-block;
  padding-left: 10px;
}

.log_content_box {
  background-color: #E8E8E8;
}

</style>