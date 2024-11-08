<script setup lang="ts">
import {getFailServlet} from "@/api/dataApi";
import {reactive, ref} from "vue";

const data: any = reactive([])
getFailServlet().then(res => {
  for (let elem of res.getContext()) {
    data.push({className: elem})
  }
})
</script>

<template>
  <el-table :data="data" height="500px">
    <el-table-column prop="className" label="映射类"/>
  </el-table>
</template>

<style scoped>
</style>