<template>
  <div class="close_connect_button">
    <el-button type="danger" @click="closeConnect">关闭连接</el-button>
  </div>
</template>

<script setup>
import {closeConnectApi} from "@/utils/manageApi";
import {Message} from "@/utils/message";

defineProps({
  status: {required: true, type: Boolean}
})

const emit = defineEmits(['update:status'])


const closeConnect = () => {
  closeConnectApi().then(res => {
    if (!res.data.msg) {
      emit('update:status', false)
      Message('消息', "关闭远程连接成功", 'success')
    } else {
      Message('消息', "存在扫描时不支持阻断关闭", 'error')
    }
  })
}
</script>

<style scoped>
.close_connect_button {
  display: inline-block;
  padding-left: 10px;
}
</style>