<template>
  <div class="middleware-data" v-if="props.middleId !== -1">
    <el-table v-if="mode ==='router'" :data="servletData" :height="props.show ?'450px':'300px'"
              :row-class-name="tableRowClassName">
      <el-table-column prop="classname" label="classname"/>
      <el-table-column type="expand">
        <template #default="props">
          <ul v-for="url in props.row.urls">{{ url }}</ul>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作">
        <template #default="props">
          <el-button type="primary" @click="handlerMark(props.row)">标记</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-table v-if="mode ==='filter'" :height="props.show ?'450px':'300px'" :data="filterData">
      <el-table-column prop="id" label="id" width="180"/>
      <el-table-column prop="priority" label="priority"/>
      <el-table-column prop="classname" label="classname"/>
      <el-table-column prop="url" label="url"/>
    </el-table>
    <el-pagination layout="prev, pager, next" :total="props.dataLen"
                   v-model:current-page.sync="page"
                   @current-change="handlerCurrentChange"
    />
  </div>
</template>
<script setup lang="ts">
import {
  apiGetSpecifiedMiddlewareFilter,
  apiGetSpecifiedMiddlewareUrlMap,
  Filter,
  Servlet, apiServletMark
} from "@/api/data";
import {ref, watch} from "vue";

const props = defineProps({
  mode: String as () => 'router' | 'filter',
  middleId: {
    type: Number,
    default: -1
  },
  dataLen: Number,
  show: Boolean,
})
const servletData = ref<Servlet[]>([])
const filterData = ref<Filter[]>([])
const page = ref<Number>()
const tableRowClassName = ({row, rowIndex}: { row: Servlet, rowIndex: number }) => {
  if (row.mark) {
    return 'warning-row'
  }
  return ''
}

const handlerMark = (servlet: Servlet) => {
  servlet.mark = !servlet.mark
  apiServletMark(servlet.id, servlet.mark)
}
const handlerCurrentChange = (currentPosition: number) => {
  page.value = currentPosition
  props.mode === 'router' ?
      apiGetSpecifiedMiddlewareUrlMap(props.middleId, (currentPosition - 1) * 10).then(res => {
        servletData.value = res
      }) :
      apiGetSpecifiedMiddlewareFilter(props.middleId, (currentPosition - 1) * 10).then(res => {
        filterData.value = res
      })
}
// 默认显示第一页
handlerCurrentChange(1)

watch(
    () => props.middleId,
    () => {
      handlerCurrentChange(1)
    }
)
watch(
    () => props.mode,
    () => {
      handlerCurrentChange(1)
    }
)
</script>
<style scoped>
.middleware-data {
  padding: 0;
  margin: 0;
}

.el-table >>> .warning-row {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}
</style>
