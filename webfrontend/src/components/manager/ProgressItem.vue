<template>
  <el-card shadow="hover">
    <p class="progress-name">进度名:</p>
    <span class="progress-item-name">{{ props.progress.name }}</span>
    <div v-if="props.progress?.message !== ''" style="display: inline-block;">
      <el-icon>
        <DArrowRight/>
      </el-icon>
      <span class="progress-item-name">{{ props.progress.message }}</span>
    </div>
    <div>
      <div v-if="props.progress?.fail">
        <span>失败原因: </span>
        <span>{{ props.progress.error }}</span>
      </div>
      <el-progress :text-inside="true" :stroke-width="25" :percentage="props.progress?.percentage"
                   :status="getStatus(props.progress)"/>
    </div>
  </el-card>
</template>
<script setup lang="ts">
import {Progress} from "@/api/progress";
import {DArrowRight} from '@element-plus/icons-vue'

const props = defineProps({
  progress: {
    type: Progress
  }
})
const getStatus = (progress: Progress) => {
  if (progress.fail) {
    return "exception"
  } else if (progress.percentage === 100) {
    return "success"
  }
  return "format"
}
</script>

<style scoped>
.progress-name {
  display: inline-block;
  font-weight: bold;
  font-size: 20px;
  padding-right: 10px;
}

.progress-item-name {
  font-size: 18px;
}
</style>
