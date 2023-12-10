<script setup>
import {reactive, ref} from "vue";

const props = defineProps({
  context: {
    type: Object,
    required: true
  }
})
const count = ref()
const tableData = reactive([])
const filterData = reactive([])
for (let [key, value] of Object.entries(props.context.urlMap)) {
  tableData.push({"url": key, "classname": value})
}
for (let elem of props.context.filterMap) {
  filterData.push(elem)
}

const TableSelect = (context) => {
  if (context.props.label === "filterMap") {
    count.value = filterData.length
  }

  if (context.props.label === "urlMap") {
    count.value = tableData.length
  }
}

</script>

<template>
  <el-tabs @tab-click="TableSelect">
    <el-tab-pane label="urlMap">
      <el-table
          :data="tableData"
          height="300px"
      >
        <el-table-column prop="url" label="路径映射"/>
        <el-table-column prop="classname" label="类名"/>
      </el-table>
    </el-tab-pane>
    <el-tab-pane label="filterMap">
      <el-table
          :data="filterData"
          height="300px"
      >
        <el-table-column prop="url" label="路径映射"/>
        <el-table-column prop="className" label="类名"/>
      </el-table>
    </el-tab-pane>
    <el-statistic title="总计个数" :value="count"/>

  </el-tabs>
</template>

<style scoped>

</style>