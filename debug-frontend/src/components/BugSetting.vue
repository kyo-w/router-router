<template>
  <el-form ref="loginForm" :model="form" :rules="rules" label-width="80px" class="login-box">
    <h3 class="login-title">参数设置</h3>
    <el-form-item label="地址" prop="address">
      <el-input type="text" placeholder="请输入debug地址" v-model="form.address"/>
    </el-form-item>
    <el-form-item label="端口" prop="port">
      <el-input type="text" placeholder="请输入debug端口" v-model="form.port"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" v-on:click="onSubmit()">连接</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import {connectStatusApi} from '../utils/manageApi';
import {getArgApi, setArg} from "../utils/settingApi";
import {Message} from "@/utils/message";

export default {
  name: "BugSetting",
  data() {
    let IpReg = /^(([1-9]?\d|1\d{2}|2[0-4]\d|25[0-5])\.){3}([1-9]?\d|1\d{2}|2[0-4]\d|25[0-5])$/,
        urlReg = /^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w\.-]+)+[\w\-\._~:/?#[\]@!\$&'\*\+,;=.]+$/,
        validateIp = (rule, value, callback) => {
          if (!IpReg.test(value)) {
            callback(new Error('地址格式异常'))
          } else {
            this.form.IpAllow = true
            callback()
          }
        },
        validatePort = (rule, value, callback) => {
          let number = parseInt(value)
          if (window.isNaN(number)) {
            callback(new Error('仅支持数字格式'))
          } else if (number <= 0 || number > 65535) {
            callback(new Error('超出端口范围'))
          } else {
            this.form.PortAllow = true
            callback()
          }
        }
    return {
      form: {
        address: '',
        port: '',
        IpAllow: false,
        PortAllow: false,
      },
      rules: {
        address: [
          {required: true, message: '地址不可为空', trigger: 'blur', allow: false},
          {validator: validateIp, trigger: 'blur'}
        ],
        port: [
          {required: true, message: '端口不可为空', trigger: 'blur', allow: false},
          {validator: validatePort, trigger: 'blur'}
        ],
      },
    }
  },
  methods: {
    onSubmit() {
      if (this.form.IpAllow && this.form.PortAllow) {
        let Form = new Object()
        Form.hostname = this.form.address;
        Form.port = this.form.port;
        setArg(Form).then(res => {
          if (res.data.code !== 200) {
            Message('错误', res.data.msg, 'error')
          } else {
            Message('信息', res.data.msg, 'success')
            this.$router.push("/main")
          }
        }).catch(err => {
          Message('错误', '内部服务异常，请检查服务端口是否正常运行', 'error')
        })
      } else {
        Message('错误', '参数异常：检查输入参数', 'error')
      }
    }
  },
  created() {
    getArgApi().then(res => {
      if (res.data.code === 200) {
        this.$router.push("/main")
      }
    })
    connectStatusApi().then(res => {
      if (res.data.code === 200) {
        this.$router.push("/main")
      } else {
      }
    })
  }
}
</script>

<style scoped>
.login-box {
  border: 1px solid #DCDFE6;
  width: 350px;
  margin: 180px auto;
  padding: 35px 35px 15px 35px;
  border-radius: 5px;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  box-shadow: 0 0 25px #909399;
  background-color: white;

}

.login-title {
  text-align: center;
  margin: 0 auto 40px auto;
  color: #303133;
}
</style>
