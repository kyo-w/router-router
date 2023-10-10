<template>
  <div class="scanner_mem_button">
    <el-button type="primary" @click="analysts">扫描内存</el-button>
  </div>
</template>

<script setup>
import {Message} from "@/utils/message";
import {analystApi} from "@/utils/manageApi";

const props = defineProps({
  status: {type: Boolean, required: true},
})

const emit = defineEmits(['update:logger'])
const analysts = () => {
  if (!props.status) {
    Message('消息', "还未连接", 'error')
  } else {
    emit('update:logger', true)
    analystApi().then(res => {
      Message('消息', res.data.msg, 'info')
    })
  }
}
</script>

<style scoped>
.scanner_mem_button {
  display: inline-block;
  padding-left: 10px;
}
</style>