<script setup>
import {ref} from "vue";
import {getContextSizeByName} from "@/api/dataApi";
import FrameworkInfo from "@/components/debug/context/FrameworkInfo.vue";

const props = defineProps({
  name: {
    type: String,
    required: true
  }
})
const currentIndex = ref({
  name: props.name,
  index: '1'
})
const contextLength = ref(0)

const handlerClick = (key) => {
  currentIndex.value.index = key.props.name
}

getContextSizeByName(props.name).then(res => {
  contextLength.value = res.getContext()
})
</script>

<template>
  <el-tabs v-model="currentIndex.index" @tab-click="handlerClick" type="border-card">
    <el-tab-pane :label="String(value)" :name="String(value)" v-for="value in contextLength" :lazy="true">
    </el-tab-pane>
    <FrameworkInfo :framework-info="currentIndex">
      <template #tableList="tableList">
        <slot name="tableList" :data="tableList.data"></slot>
      </template>
    </FrameworkInfo>
  </el-tabs>
</template>

<style scoped>
</style>