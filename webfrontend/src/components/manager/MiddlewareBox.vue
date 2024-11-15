<template>
  <div v-if="props.middleList?.length > 0">
    <el-descriptions
        :column="3"
        border
    >
      <template #extra>
        <el-button class="el-extra" type="primary" @click="infoShow= !infoShow">隐藏</el-button>
        <div class="el-extra">
          <el-pagination background layout="prev, pager, next" :total="(props.middleList?.length) *10"
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
          {{ props.middleList[index].type }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              物理路径
            </div>
          </template>
          {{ props.middleList[index].physicalPath }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              版本
            </div>
          </template>
          {{ props.middleList[index].version }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              虚拟路径
            </div>
          </template>
          {{ props.middleList[index].virtualPath === '' ? '/' : props.middleList[index].virtualPath}}
        </el-descriptions-item>
      </div>
    </el-descriptions>
    <el-menu
        class="el-menu-demo"
        mode="horizontal"
        default-active="1"
    >
      <el-menu-item index="1" @click="handlerMiddleData('router')">路由</el-menu-item>
      <el-menu-item index="2" @click="handlerMiddleData('filter')">过滤器</el-menu-item>
    </el-menu>
    <middleware-data :mode='mode' :middle-id="props.middleList[index].id" :data-len="middleDataLen" :show="!infoShow"/>
  </div>
</template>
<script setup lang="ts">
import {
  apiGetSpecifiedMiddlewareFilterCount,
  apiGetSpecifiedMiddlewareUrlMapCount, Middleware
} from "@/api/data";
import {ref, watch} from "vue";
import MiddlewareData from "@/components/manager/MiddlewareData.vue";

const mode = ref<'router' | 'filter'>('router')
const index = ref<number>(0)
const middleDataLen = ref<number>(0)
const infoShow = ref<Boolean>(true)
const props = defineProps({
  middleList: {
    type: Object as () => Middleware[],
    required: true,
    default: () => ([])
  }
})
watch(
    () => props.middleList,
    (newMiddleList) => {
      apiGetSpecifiedMiddlewareUrlMapCount(newMiddleList[index.value].id).then(res => middleDataLen.value = res)
    }
)
const handlerSizeChange = (currentIndex: number) => {
  index.value = currentIndex - 1
  apiGetSpecifiedMiddlewareUrlMapCount(props.middleList[index.value].id).then(res => middleDataLen.value = res)
}
const handlerMiddleData = (selectMode: 'router' | 'filter') => {
  mode.value = selectMode
  if (mode.value == 'router') {
    apiGetSpecifiedMiddlewareUrlMapCount(props.middleList[index.value].id).then(res => middleDataLen.value = res)
  } else {
    apiGetSpecifiedMiddlewareFilterCount(props.middleList[index.value].id).then(res => middleDataLen.value = res)
  }
}
</script>
<style scoped>
.el-extra {
  display: inline-block;
  margin-right: 20px;
}
</style>
