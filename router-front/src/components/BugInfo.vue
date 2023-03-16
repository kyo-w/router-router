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
          <el-form-item label="目标网站" prop="url">
            <el-input v-model="form.url" placeholder="目标网站可访问的url(全路径)"></el-input>
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
import {setArg} from "../utils/settingApi";

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
      },
      validateUrl = (rule, value, callback) => {
        if (!urlReg.test(value)) {
          callback(new Error('url地址格式异常'))
        } else {
          this.form.urlAllow = true
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
        url: [
          {required: true, message: 'url格式不正确', trigger: 'blur', allow: false},
          {validator: validateUrl, trigger: 'blur'}
        ],
      },
    }
  },
  methods:{
    onSubmit() {
      if (this.form.IpAllow && this.form.PortAllow && this.form.urlAllow) {
        let Form = new Object()
        Form.address = this.form.address;
        Form.port = this.form.port;
        Form.url = this.form.url;
        setArg(Form).then(res => {
          if (res.data.code !== 200) {
            this.$message.error(res.data.msg);
          } else {
            this.$message.success(res.data.msg);
            this.$router.push("/main")
          }
        }).catch(err => {
          this.$message.error("内部服务异常，请检查服务端口是否正常云心");
        })
      } else {
        this.$message.error("参数异常：检查输入参数")
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
