<template>
  <div class="setting_button">
    <el-button type="danger" @click="settingConnectArg">重置调试参数</el-button>
  </div>
</template>

<script setup>
import {Message} from "@/utils/message";
import {cleanArgApi} from "@/utils/settingApi";
import {useRouter} from "vue-router";

const router = useRouter()
const props = defineProps({
  status: {type: Boolean, required: true}
})

const settingConnectArg = () => {
  if (props.status) {
    Message('功能使用错误', '设置前需要断开连接', 'error')
  } else {
    cleanArgApi().then(res=>{
      router.push('/')
    })
  }
}
</script>

<style scoped>
.setting_button {
  display: inline-block;
  padding-left: 10px;
}
</style>