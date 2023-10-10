<template>
  <div class="connect_debug_button">
    <el-button type="primary" @click="connectDebug">连接</el-button>
  </div>
</template>

<script setup>

import {Message} from "@/utils/message";
import {connectDebugApi} from "@/utils/manageApi";
const emit = defineEmits(['update:status', 'update:logger'])
const connectDebug = () => {
  connectDebugApi().then(res => {
    if (res.data.code === 200) {
      emit('update:status', true)
      emit('update:logger', true)
      Message('消息', res.data.msg, 'info')
    } else if (res.data.code === 300) {
      Message('消息', res.data.msg, 'warning')
    } else if (res.data.code === 400) {
      Message('消息', res.data.msg, 'error')
    }
  }).catch(err => {
    Message('网络异常', '内部服务异常，检查服务是否正常运行', 'error')
  })
}
</script>

<style scoped>
.connect_debug_button {
  display: inline-block;
  padding-left: 10px;
}
</style>