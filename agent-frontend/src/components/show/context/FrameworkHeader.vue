<script setup>
const props = defineProps({
  framework: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <el-descriptions
      title="基础信息"
      direction="vertical"
      :column="2"
      border
  >
    <el-descriptions-item label="版本">9.0.0</el-descriptions-item>
    <el-descriptions-item label="虚拟根路径">{{ props.framework.virtualPath }}</el-descriptions-item>
  </el-descriptions>
</template>

<style scoped>

</style>