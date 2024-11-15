<template>
  <div class="project-title">
    <div class="float-div project-title-name">项目</div>
    <div v-if="!props.project.isEmpty()" class="float-div">已选择: {{ props.project.name }}</div>
  </div>
</template>
<script setup lang="ts">
import {Project} from "@/api/project";

const props = defineProps({
  project: Project
})
</script>
<style scoped>
.project-title {
  padding: 0;
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.project-title-name {
  font-size: 24px;
  font-weight: bold;
}

.float-div {
  display: inline-block;
  font-size: 24px;
  font-weight: bold;
}
</style>
