<template>
  <div class="export_data_button">
    <el-button type="danger" @click="exportData">一键导出(HTML)</el-button>
  </div>
</template>

<script setup>
import {Message} from "@/utils/message";
import {exportAllApi} from "@/utils/dataApi";
const props = defineProps({
  targets : {required: true, type: Object}
})

const exportData = () => {
  if (props.targets.length === 0) {
    Message('数据', '无数据', 'error')
  } else {
    exportAllApi().then(res => {
      let blob = new Blob([res.data]);
      let url = window.URL.createObjectURL(blob);
      let a = document.createElement('a');
      a.href = url;
      a.download = 'output.xls';
      a.click();
      window.URL.revokeObjectURL(url);
    })
  }
}
</script>

<style scoped>
.export_data_button {
  display: inline-block;
  padding-left: 10px;
}
</style>