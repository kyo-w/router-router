<template>
  <el-descriptions :border="true">
    <el-descriptions-item label="主机">{{ props.project.hostname }}</el-descriptions-item>
    <el-descriptions-item label="端口号">{{ props.project.port }}</el-descriptions-item>
    <el-descriptions-item label="创建日期">{{ props.project.createTime }}</el-descriptions-item>
  </el-descriptions>
</template>
<script setup lang="ts">
import {Project} from "@/api/project";

const props = defineProps({
  project:Project
})
</script>

<style scoped>

</style>
