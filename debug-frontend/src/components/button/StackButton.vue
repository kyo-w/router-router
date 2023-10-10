<template>
  <div class="stack_button">
    <el-button type="primary" @click="openStack">堆栈分析器</el-button>
    <el-drawer v-model="connectLogDrawer" size="60%">
      <template #header="{titleId, titleClass }">
        <h2 :id="titleId" :class="titleClass">堆栈分析器[测试性功能：容易造成服务器假死机状态，性能堪忧]</h2>
      </template>
      <div class="log_content_box">
        <el-switch
            class="status" v-model="statusFlag"
            inline-prompt active-text="启用" inactive-text="未启用"
            style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949; padding-right: 10px;"/>
        <el-button type="primary" @click="startListen">启动堆栈监听</el-button>
        <el-button type="primary" @click="cleanCache">清除记录</el-button>
        <el-button type="danger" @click="closeListen">关闭堆栈监听</el-button>

        <el-table :data="stackList.list" style="width: 100%" ref="refTable" @row-click="clickTable">
          <el-table-column prop="name" label="业务类"/>
          <el-table-column type="expand">
            <template #default="props">
              <div>
                <div v-for="(value,key) in props.row.stacks">[{{ key }}]: [{{ value }}]</div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import config from "@/config";
import {cleanCacheApi, endListenApi, getCacheDataApi, startListenApi} from "@/utils/stackApi";
import {Message} from "@/utils/message";

let socket = ref()
let connectLogDrawer = ref(false)
let stackList = reactive({list: []})
const refTable = ref(null)
const statusFlag = ref(false)

const openStack = () => {
  connectLogDrawer.value = true
}
const clickTable = (row, index, e) => {
  refTable.value.toggleRowExpansion(row)
}

const startListen = () => {
  startListenApi().then(res => {
    if (res.data.code !== 200) {
      Message('信息', res.data.msg, 'warning')
    } else {
      statusFlag.value = true
      Message('信息', res.data.msg, 'success')
    }
  })
}
const closeListen = () => {
  endListenApi().then(res => {
    statusFlag.value = false
  })
}

const cleanCache = () => {
  cleanCacheApi().then(res => {
    stackList.list = []
  })
}

onMounted(() => {
  socket.value = new WebSocket(config.GetStackWsLocation())
  socket.value.onmessage = (event) => {
    stackList.list.push(JSON.parse(event.data))
  }
})

const onCreate = () => {
  getCacheDataApi().then(res => {
    stackList.list = res.data.msg
  })
}
onCreate()
</script>

<style scoped>
.stack_button {
  display: inline-block;
  padding-left: 10px;
}
</style>