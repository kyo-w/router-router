<template>
  <el-row :gutter="10" class="top_collection">
    <el-col :span="10">
      <div>
        <el-card class="box-card">
          <div class="info_div">
            <img class="info_img" src="../assets/IP.png" alt="your-image">
            <span class="text">{{ ip }}</span>
          </div>
          <div class="info_div">
            <img class="info_img" src="../assets/port.png" alt="your-image">
            <span class="text">{{ port }}</span>
          </div>
        </el-card>
      </div>
    </el-col>
    <el-col :span="14">
      <div>
        <el-card class="box-card">
          <el-switch
              class="status"
              v-model="statusFlag"
              inline-prompt
              active-text="连接"
              inactive-text="未连接"
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              :before-change="beforeChange"
          />
          <ConnectDebugButton @update:logger="updateLogger" @update:status="updateStatus"/>
          <ScannerMemButton v-model:status="statusFlag" @update:logger="updateLogger"/>
          <StackButton/>
          <LoggerButton v-model:logger="loggerDialogVisible"/>
          <hr/>
          <SettingButton v-model:status="statusFlag"/>
          <EmptyDataButton @close:target="cleanData"/>
          <ExportDataButton v-model:targets="targets"/>
          <CloseConnectButton v-model:status="statusFlag"/>
        </el-card>
      </div>
    </el-col>
  </el-row>
  <el-row :gutter="10">
    <el-col :span="4">
      <div style="height: 500px">
        <el-menu class="el-menu-demo" @select="handleSelect" v-for="item in targets">
          <el-menu-item :index="item"><img :src="'/static/' + item + '.png'" style="margin-right: 20px" alt="">
            <span class="button_middleware">{{ item }}</span>
          </el-menu-item>
        </el-menu>
      </div>
    </el-col>
    <el-col :span="20">
      <div class="grid-content ep-bg-purple" style="height: 500px">
        <router-view/>
      </div>
    </el-col>
  </el-row>
</template>

<script setup>
import {connectStatusApi} from "@/utils/manageApi";
import {getArgApi} from "@/utils/settingApi";
import {getListDataApi} from "@/utils/dataApi";
import {Message} from "@/utils/message";
import StackButton from "@/components/button/StackButton.vue";
import LoggerButton from "@/components/button/LoggerButton.vue";
import ConnectDebugButton from "@/components/button/ConnectDebugButton.vue";
import SettingButton from "@/components/button/SettingButton.vue";
import EmptyDataButton from "@/components/button/EmptyDataButton.vue";
import ExportDataButton from "@/components/button/ExportDataButton.vue";
import CloseConnectButton from "@/components/button/CloseConnectButton.vue";
import {onBeforeUnmount, reactive, ref, inject } from 'vue'
import {useRouter} from 'vue-router'
import ScannerMemButton from "@/components/button/ScannerMemButton.vue";
import config from "@/config";

let ip = ref()
let port = ref()

let statusFlag = ref(false)
let loggerDialogVisible = ref(false)
let targets = reactive([])
let dialogVisible = ref(false)
let socket = ref()
const router = useRouter()
const reload = inject('reload')

const getListData = () => {
  getListDataApi().then(res => {
    if (res.data.msg.spring) {
      targets.push("spring")
    }
    if (res.data.msg.struts) {
      targets.push("struts")
    }
    if (res.data.msg.tomcat) {
      targets.push("tomcat")
    }
    if (res.data.msg.jetty) {
      targets.push("jetty")
    }
    if (res.data.msg.jersey) {
      targets.push("jersey")
    }
    if (res.data.msg.filter) {
      targets.push("filter")
    }
  })
}
const beforeChange = () => {
  return false
}

const handleSelect = (key) => {
  router.push("/main/" + key)
}

const updateLogger = (value) => {
  loggerDialogVisible.value = value
}
const updateStatus = (value) => {
  statusFlag.value = value
}

const cleanData = () => {
  targets.length = 0
  reload()
}

const onCreate = () => {
  socket.value = new WebSocket(config.GetEventWsLocation())
  socket.value.onmessage = (event) => {
    targets.push(event.data)
  }
  getArgApi().then(res => {
    if (res.data.code !== 200) {
      Message('警告', res.data.msg, 'warning')
      router.push("/index")
    } else {
      ip.value = res.data.msg.hostname
      port.value = res.data.msg.port
    }
  })
  getListData()
  connectStatusApi().then(res => {
    statusFlag.value = res.data.code === 200;
  })
}
onBeforeUnmount(() => {
  socket.value.close()
})
onCreate()
</script>

<style scoped>

info_div {
  display: flex;
  align-items: center;
}

.info_img {
  display: inline-block;
  vertical-align: middle;
  width: 40px;
  height: 40px;
  padding-right: 20px;
}

.text {
  display: inline-block;
  vertical-align: middle;
  font-size: 20px;
}

.status {
  padding-left: 20px;
  padding-right: 20px;
}

img {
  width: 50px;
  height: 50px;
}

.top_collection {
  padding-bottom: 10px;
}

.button_middleware {
  color: #606266;
  font-size: 20px;
  font-family: STXihei, "Microsoft YaHei", serif;
}
</style>
