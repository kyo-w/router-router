<template>
  <div>
    <el-icon v-if="props.show"><Select/></el-icon>
    <div class="project-name-icon" :class="{projectSelect: props.show}">
      {{ props.project.name }}
    </div>
  </div>
</template>
<script setup lang="ts">
import {Select} from "@element-plus/icons-vue"
import {Project} from "@/api/project";

const props = defineProps({
  show: Boolean,
  project: Project
})
</script>
<style scoped>
.project-name-icon {
  margin-left: 10px;
  display: inline-block;
  color: #4b4b52;
  font-size: 18px;
}

.projectSelect {
  color: #6b89cb;
}
</style>
