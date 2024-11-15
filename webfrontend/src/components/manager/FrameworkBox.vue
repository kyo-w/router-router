<template>
  <div v-if="props.frameworkList?.length > 0">
    <el-descriptions
        :column="3"
        border
    >
      <template #extra>
        <el-button class="el-extra" type="primary" @click="infoShow= !infoShow">隐藏</el-button>
        <div class="el-extra">
          <el-pagination background layout="prev, pager, next" :total="(props.frameworkList?.length) *10"
                         @current-change="handlerSizeChange"/>
        </div>
      </template>
      <div v-if="infoShow">
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              类型
            </div>
          </template>
          {{ props.frameworkList[index].type }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              上下文路径
            </div>
          </template>
          {{ props.frameworkList[index].contextPath }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              版本
            </div>
          </template>
          {{ props.frameworkList[index].version }}
        </el-descriptions-item>
      </div>
    </el-descriptions>
    <el-menu
        class="el-menu-demo"
        mode="horizontal"
        default-active="1"
    >
    </el-menu>
    <framework-data :framework-id="props.frameworkList[index].id" :data-len="frameworkDataLen" :show="!infoShow"/>
  </div>

</template>
<script setup lang="ts">
import {apiGetSpecifiedFrameworkUrlMapCount, Framework} from "@/api/data";
import {ref, watch} from "vue";
import FrameworkData from "@/components/manager/FrameworkData.vue";

const props = defineProps({
  frameworkList: {
    type: Object as () => Framework[],
    required: true,
    default: () => ([])
  },
})
const index = ref<number>(0)
const infoShow = ref<Boolean>(true)
const frameworkDataLen = ref<number>(0)

watch(
    () => props.frameworkList,
    (newMiddleList) => {
      apiGetSpecifiedFrameworkUrlMapCount(newMiddleList[index.value].id).then(res => frameworkDataLen.value = res)
    }
)
const handlerSizeChange = (currentIndex: number) => {
  index.value = currentIndex - 1
  apiGetSpecifiedFrameworkUrlMapCount(props.frameworkList[index.value].id).then(res => frameworkDataLen.value = res)
}
</script>
<style scoped>
.el-extra {
  display: inline-block;
  margin-right: 20px;
}
</style>
