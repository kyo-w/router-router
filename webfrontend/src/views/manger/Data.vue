<template>
  <div class="data-menu-select">
    <el-menu
        mode="horizontal"
        default-active="0"
    >
      <el-menu-item v-for="(target,i) in middlewareTypes" :index="i.toString()"
                    @click="handlerMiddleware(target)">
      <span class="type-name">
        {{ target }}
      </span>
      </el-menu-item>
      <el-menu-item v-for="(target,i) in frameworkTypes"
                    :index="(i +middlewareTypes.length).toString()"
                    @click="handlerFramework(target)">
      <span class="type-name">
        {{ target }}
      </span>
      </el-menu-item>
    </el-menu>
  </div>
  <div>
    <mg-data ref="mgDataView" :data-type="dataType"
             :middleware-data="middlewareData"
             :framework-data="frameworkData"/>
  </div>
</template>
<script setup lang="ts">
import {ref, watch} from "vue";
import {
  apiGetFrameworkAllData,
  apiGetMiddlewareAllData, apiGetSpecifiedFrameworkPanel, apiGetSpecifiedMiddleware, Framework,
  Middleware
} from "@/api/data";
import MgData from "@/components/manager/MgData.vue";

const middlewareTypes = ref<string[]>([])
const frameworkTypes = ref<string[]>([])
const dataType = ref<'framework' | 'middleware'>('middleware')
const middlewareData = ref<Middleware[]>([])
const frameworkData = ref<Framework[]>([])
apiGetMiddlewareAllData().then(res => middlewareTypes.value = res)
apiGetFrameworkAllData().then(res => frameworkTypes.value = res)

watch(middlewareTypes, (newMiddlewareTypes): void => {
  dataType.value = 'middleware'
  if (newMiddlewareTypes.length > 0) {
    handlerMiddleware(newMiddlewareTypes[0])
  }
})
const handlerMiddleware = (target: string) => {
  dataType.value = 'middleware'
  apiGetSpecifiedMiddleware(target).then(res => middlewareData.value = res)
}
const handlerFramework = (target: string) => {
  dataType.value = 'framework'
  apiGetSpecifiedFrameworkPanel(target).then(res => frameworkData.value = res)
}
</script>
<style scoped>
.type-name {
  font-weight: bolder;
  font-size: 18px;
}

.data-menu-select {
  margin-bottom: 20px;
}
</style>
