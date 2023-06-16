<template>
  <div>
    <fieldset>
      <legend>调试信息</legend>
      <el-form :model="form" label-width="80px" :rules="rules">
        <el-col :span="18">
          <el-form-item label="地址" prop="address">
            <el-input v-model="form.address" placeholder="请输入debug地址" ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="18">
          <el-form-item label="端口" prop="port">
            <el-input v-model="form.port" placeholder="请输入debug端口"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="18">
          <el-form-item>
            <el-button type="primary" @click="onSubmit">修改</el-button>
          </el-form-item>
        </el-col>
      </el-form>
    </fieldset>
  </div>
</template>

<script>
import {setArg} from "../utils/settingApi"
import {Message} from "@/utils/message";

export default {
  name: "BugInfo",
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
        url: '',
        IpAllow: false,
        PortAllow: false,
        urlAllow: false
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
  methods:{
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
          }
          this.$router.push('/')
        }).catch(err => {
          Message('错误', '内部服务异常，请检查服务端口是否正常运行', 'error')
        })
      } else {
        Message('错误', '参数异常：检查输入参数', 'error')
      }
    }
  }
}
</script>

<style scoped>
fieldset {
  border: 2px solid #DCDFE6;
  text-align: left;
  border-radius: 8px;
  margin: 0 auto;
  width: 80%;
}
</style>
