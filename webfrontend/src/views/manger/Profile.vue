<template>
  <el-descriptions
      class="margin-top"
      title="调试参数"
      :column="3"
      size="large"
      border
  >
    <el-descriptions-item>
      <template #label>
        <div class="cell-item">
          项目名
        </div>
      </template>
      {{ currentProject?.name }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <div class="cell-item">
          主机名
        </div>
      </template>
      {{ currentProject?.hostname }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <div class="cell-item">
          端口
        </div>
      </template>
      {{ currentProject?.port }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <div class="cell-item">
          创建时间
        </div>
      </template>
      {{ currentProject?.createTime }}
    </el-descriptions-item>
  </el-descriptions>
</template>
<script setup lang="ts">
import {apiGetCurrentProject, Project} from "@/api/project";
import {ref} from "vue";

const currentProject = ref<Project>()
apiGetCurrentProject().then(result => currentProject.value = result)
</script>
<style scoped>
.cell-item {
  font-size: 16px;
}
</style>
