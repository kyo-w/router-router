<script setup>
import {reactive, ref} from "vue";
import {createDebugInfoWs} from "@/utils/routerService";
import {cleanLogData, getLogData} from "@/api/logApi";

const logList = reactive({data: []})
const currentPage = ref(1)
const pageSize = ref(10)

// 调用清空日志API
const clearLog = () => {
  cleanLogData()
  logList.data.length = 0
}

const getLength = () => {
  if (logList.data.length < 10) {
    return 1;
  } else {
    return Math.floor(logList.data.length / 10) * 10
  }
}
const changePage = (page) => {
  currentPage.value = page
}


getLogData().then(res => {
  if (res.getContext() != null) {
    for (let log of res.getContext()) {
      log.date = new Date(log.date)
      logList.data.push(log)
    }
  }
})
createDebugInfoWs(event => {
  let log = JSON.parse(event.data)
  log.date = new Date(log.date)
  logList.data.push(log)
})
</script>

<template>
  <div>
    <div v-if="logList.data.length !== 0" v-cloak>
      <el-button @click="clearLog" type="primary" style="margin-bottom: 10px">清空输出</el-button>
    </div>
    <div id="context" v-cloak>
      <el-table
          :data="logList.data.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
          height="600px"
      >
        <el-table-column label="状态" prop="debugType" width="100px"/>
        <el-table-column label="时间" prop="date" width="200px"/>
        <el-table-column label="信息" prop="debugMessage"/>
      </el-table>
      <el-pagination class="flat" background layout="prev, pager, next" @current-change="changePage"
                     @next-click="changePage" @prev-click="changePage" :total="getLength()"
                     :hide-on-single-page="logList.data.length === 0"
      />
    </div>
  </div>
</template>

<style scoped>
#context::-webkit-scrollbar {
  display: none;
}

[v-cloak] {
  display: none;
}
</style>