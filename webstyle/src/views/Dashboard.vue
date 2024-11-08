<script setup lang="ts">
import {h, reactive, ref} from "vue";
import Theme from "@/components/Theme.vue";
import {connectTargetVmApi, mgConnectOpen} from "@/api/manageApi";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";
import {getCurrentInstance} from "vue";

const getTheme = getCurrentInstance().appContext.config.globalProperties.getTheme

const router = useRouter()
const args = reactive({
  ip: null,
  port: null
})
const load = ref(false)
const allowConnect = ref(false)
const validatePort = (rule: any, value: any, callback: any) => {
  let number = parseInt(value)
  if (window.isNaN(number)) {
    callback(new Error('仅支持数字格式'))
  } else if (number <= 0 || number > 65535) {
    callback(new Error('超出端口范围'))
  } else {
    args.port = value
    allowConnect.value = true
    callback()
  }
}
const rules = {
  port: [
    {required: true, message: '端口格式异常', trigger: 'blur', allow: false},
    {validator: validatePort, trigger: 'blur'}
  ],
}

const connectTarget = () => {
  if (!allowConnect.value) {
    ElMessage.error("请先设置参数")
    return
  }
  load.value = true
  connectTargetVmApi({hostname: args.ip, port: args.port, timeout: 3000})
      .then(res => {
        if (!res.isWant()) {
          ElMessage.error(res.getContext())
        } else {
          router.push('/router/setting')
        }
      }).finally(() => {
    load.value = false
  })
}
</script>

<template>
  <div class="connect_body" v-loading="load">
    <Theme/>
    <img :src="require('@/assets/bird' + getTheme() + '.png')">
    <div class="connect_context">
      <h2 class="connect_title">连接VM对象</h2>
      <el-form
          :model="args"
          :rules="rules"
          label-position="left"
          label-width="40px"
      >
        <el-form-item label="ip" prop="address">
          <el-input v-model="args.ip" type="text" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="port" prop="port">
          <el-input v-model="args.port" type="text" autocomplete="off"/>
        </el-form-item>
        <el-button size="large" type="primary" @click="connectTarget">连接</el-button>
      </el-form>
    </div>
  </div>
</template>
<style scoped>
.connect_body {
  position: absolute;
  width: 100%;
  height: 100%;
}

.connect_title {
  margin-bottom: 20px;
}

.connect_context {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -90%);
  text-align: center;
}
</style>