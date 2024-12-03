<template>
  <div class="framework-data" v-if="props.frameworkId !== -1">
    <el-table :data="handlerData" :height="props.show ?'450px':'300px'"
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
    <el-pagination layout="prev, pager, next" :total="props.dataLen"
                   v-model:current-page.sync="page"
                   @current-change="handlerCurrentChange"
    />
  </div>
</template>
<script setup lang="ts">

import {
  apiHandlerMark,
  apiGetSpecifiedFrameworkUrlMap,
  Handler
} from "@/api/data";
import {ref} from "vue";
import {watch} from "vue";

const props = defineProps({
  frameworkId: {
    type: Number,
    default: -1
  },
  dataLen: Number,
  show: Boolean
})

const page = ref<Number>(1)
const handlerData = ref<Handler[]>([])

const tableRowClassName = ({row, rowIndex}: { row: Handler, rowIndex: number }) => {
  if (row.mark) {
    return 'warning-row'
  }
  return ''
}

const handlerCurrentChange = (currentPosition: number) => {
  page.value = currentPosition
  apiGetSpecifiedFrameworkUrlMap(props.frameworkId, (currentPosition - 1) * 10).then(res => handlerData.value = res)
}
handlerCurrentChange(1)
watch(
    () => props.frameworkId,
    () => {
      handlerCurrentChange(1)
    }
)

const handlerMark = (handler: Handler) => {
  handler.mark = !handler.mark
  apiHandlerMark(handler.id, handler.mark)
}
</script>
<style scoped>
.framework-data {
  padding: 0;
  margin: 0;
}

.el-table >>> .warning-row {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}
</style>
