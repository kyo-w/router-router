<template>
  <div class="empty_data_button">
    <el-button type="danger" @click="cleanData">清空数据</el-button>
  </div>
</template>

<script setup>
import {cleanDataApi} from "@/utils/manageApi";
import {useRouter} from "vue-router";

const router = useRouter()
const emit = defineEmits(['close:target'])
const cleanData = () => {
  cleanDataApi()
  emit('close:target')
  router.push("/main")
}
</script>

<style scoped>
.empty_data_button {
  display: inline-block;
  padding-left: 10px;
}
</style>