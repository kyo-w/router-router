<script setup>

import {reactive, ref} from "vue";

const props = defineProps({
  context: {
    type: Object,
    required: true
  }
})
const jspList = reactive([])
if (props.context.jspMap !== null) {
  for (let [key, value] of Object.entries(props.context.jspMap)) {
    jspList.push({'url': key, 'path': value})
  }
}
const drawer = ref(false)
</script>

<template>
  <el-descriptions
      title="基础信息"
      direction="vertical"
      :column="2"
      border
  >
    <template #extra>
      <el-button type="primary" @click="drawer= true">JSP映射</el-button>
      <el-drawer v-model="drawer" size="50%">
        <div>
          <el-table :data="jspList">
            <el-table-column prop="url" label="映射"/>
            <el-table-column prop="path" label="物理地址"/>
          </el-table>
        </div>
      </el-drawer>
    </template>
    <el-descriptions-item label="版本">9.0.0</el-descriptions-item>
    <el-descriptions-item label="虚拟根路径">{{ props.context.virtualPath }}</el-descriptions-item>
    <el-descriptions-item label="物理根目录路径">{{ props.context.rootPath }}</el-descriptions-item>

  </el-descriptions>
</template>

<style scoped>

</style>