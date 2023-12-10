<script setup>
import {reactive, ref} from "vue";
import {cleanStackApi, getStack} from "@/utils/DataApi";

const refTable = ref(null)
const stackData = reactive([])
window.routersocket.stack.onmessage((event) => {
  stackData.push(JSON.parse(event.data))
})
const clickTable = (row, index, e) => {
  refTable.value.toggleRowExpansion(row)
}
const setup = () => {
  getStack().then(res => {
    for (let elem of res.data.msg) {
      stackData.push(elem)
    }
  })
}
const cleanStack = () => {
  cleanStackApi()
  stackData.length = 0
}
setup()
</script>

<template>
  <el-button type="primary" @click="cleanStack" style="margin-bottom: 10px">清空请求记录</el-button>
  <el-table :data="stackData" ref="refTable" @row-click="clickTable">
    <el-table-column type="expand">
      <template #default="props">
        <div>
          <h3>请求方法: {{ props.row.method }}</h3>
          <h3>过滤器: {{ props.row.filterList }}</h3>
          <h3>堆栈</h3>
          <el-table :data="props.row.stackTraces">
            <el-table-column label="类名" prop="className"/>
            <el-table-column label="方法" prop="methodName"/>
          </el-table>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="请求" prop="url"/>
    <el-table-column label="调用类" prop="servlet"/>
  </el-table>
</template>

<style scoped>

</style>