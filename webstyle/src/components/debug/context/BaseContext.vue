<script setup>
import {ref} from "vue";
import {getContextSizeByName} from "@/api/dataApi";
import ContextInfo from "@/components/debug/context/ContextInfo.vue";

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
  <el-tabs v-model="currentIndex.index" @tab-click="handlerClick" class="tabs" type="border-card">
    <el-tab-pane v-for="value in contextLength" :label="String(value)" :lazy="true"
                 :name="String(value)">
    </el-tab-pane>
    <ContextInfo :context-info="currentIndex"/>
  </el-tabs>
</template>

<style scoped>
</style>