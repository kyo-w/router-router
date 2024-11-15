<template>
  <div class="mg-header">
    <div class="mg-connect-status">
      <span>连接状态</span>
      <el-switch size="large" v-model="connectStatus" :disabled="true"/>
    </div>
    <div class="mg-connect-button">
      <el-button :loading="connectButton" type="primary" @click="handlerConnect"
                 :disabled="connectStatus ===true">连接</el-button>
      <el-button :loading="disConnectButton" type="danger" @click="handlerDisconnect"
                 :disabled="connectStatus ===false || taskStatus">
        断开连接
      </el-button>
      <el-button :loading="startAnalystsButton" type="primary" @click="handlerStartAnalysts"
                 :disabled="connectStatus ===false || taskStatus">开始分析
      </el-button>
      <el-button :loading="stopAnalystsButton" type="danger" @click="handlerStopAnalysts"
                 :disabled="taskStatus === false">
        停止分析
      </el-button>
      <el-button type="danger" @click="logout">退出项目</el-button>
    </div>
  </div>
</template>
<script setup lang="ts">
import {ref} from "vue"
import {apiGetJDWPStatus, apiConnectJDWP, apiCloseJDWP} from "@/api/jdwp"
import {apiAnalystStatus, apiStartAnalysts, apiStopAnalysts} from "@/api/task"
import {ElMessage} from "element-plus"
import {apiLogoutProject} from "@/api/project";
import {useRouter} from 'vue-router'

const router = useRouter()
const connectStatus = ref<Boolean>(false)
const taskStatus = ref<Boolean>(false)
const connectButton = ref<Boolean>(false)
const disConnectButton = ref<Boolean>(false)
const startAnalystsButton = ref<Boolean>(false)
const stopAnalystsButton = ref<Boolean>(false)
apiGetJDWPStatus().then(exist => connectStatus.value = exist)
apiAnalystStatus().then(exist => taskStatus.value = exist)
const handlerConnect = () => {
  connectButton.value = true
  apiConnectJDWP().then(success => {
    connectStatus.value = success
    connectButton.value = false
  })
}

const handlerDisconnect = () => {
  disConnectButton.value = true
  apiCloseJDWP().then(res => {
    disConnectButton.value = false
    connectStatus.value = false
  })
}

const handlerStartAnalysts = () => {
  startAnalystsButton.value = true
  apiStartAnalysts().then(status => {
    taskStatus.value = status
    startAnalystsButton.value = status
  })
}
const handlerStopAnalysts = () => {
  stopAnalystsButton.value = true
  apiStopAnalysts().then(res => {
    taskStatus.value = false
    stopAnalystsButton.value = false
  })
}

const logout = () => {
  if (taskStatus.value) {
    ElMessage({
      type: 'warning',
      message: '任务还未完成',
    })
    return
  }
  if (connectStatus.value) {
    ElMessage({
      type: 'warning',
      message: '请先断开连接',
    })
    return
  }
  apiLogoutProject().then(res => router.push('/'))
}
</script>

<style scoped>
.mg-header {
  padding-left: 20px;
  height: 100px;
  line-height: 100px;
  position: relative;
}

.mg-connect-status {
  font-weight: bold;
  font-size: 20px;
  margin-right: 15px;
  display: inline-block;
}

.mg-connect-button {
  display: inline-block;
  left: 40%;
  position: absolute;
}
</style>
