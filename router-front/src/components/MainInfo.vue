<template>
  <div class="root">
    <div v-if="show">
      <el-container
        v-loading="analyse"
        :element-loading-text="analyLog"
      >
        <el-header height="">
          <el-col :span="8" class="complex">
            <div class="ip_port">
              <span>ip: </span>
              <el-tag id="ip">{{ this.ip }}</el-tag>
              <span>port: </span>
              <el-tag>{{ this.port }}</el-tag>
            </div>
            <el-divider></el-divider>
            <div class="url">url:
              <el-tag>{{ this.url }}</el-tag>
            </div>
          </el-col>
          <el-col :span="4" class="status">
            <div v-if="statusFlag">
              <span>status:<el-tag type="success">connected</el-tag></span>
            </div>
            <div v-else>
              <span>status:<el-tag type="danger">unconnected</el-tag></span>
            </div>
          </el-col>
          <el-col :span="12" class="button">
            <el-button type="primary" @click="settingConnectArg">设置</el-button>
            <el-button type="danger" @click="emptybutton">未授权探测</el-button>
            <el-button type="primary" @click="connectdebug">连接与分析</el-button>
            <el-button type="danger" @click="disConnectDebug">断开连接</el-button>
            <el-button type="danger" @click="cleanData">置空</el-button>
            <el-button type="danger" @click="exportData">一键导出(xls)</el-button>
          </el-col>
        </el-header>
        <el-main>
          <el-col :span="4">
            <el-menu class="el-menu-demo" mode="el-menu-vertical-demo" @select="handleSelect" v-for="item in targets">
              <el-menu-item :index="item"><img :src="'../../static/' + item + '.png'">{{ item }}</el-menu-item>
            </el-menu>

          </el-col>
          <el-col :span="20">
            <router-view/>
          </el-col>
        </el-main>
      </el-container>
    </div>
    <el-dialog
      :visible.sync="dialogVisible"
      width="40%">
      <bug-info/>
    </el-dialog>
  </div>
</template>

<script>
import {
  connectDebugApi,
  runAnalyApi,
  analyStatusApi,
  cleanDataApi,
  stopConnectApi,
  connectStatusApi
} from "../utils/manageApi";
import {getArgApi} from "../utils/settingApi";
import {delay} from "../utils/utils";
import {existDataApi, getListDataApi, exportAllApi} from "../utils/dataApi";

import BugInfo from './BugInfo.vue';

export default {
  name: "MainInfo",
  inject: ['reload'],
  data() {
    return {
      ip: "127.0.0.1",
      port: "5005",
      url: "",
      statusFlag: false,
      show: false,
      analyse: false,
      analyLog: "",
      targets: [],
      dialogVisible: false
    }
  },
  methods: {
    handleSelect(key, keyPath) {
      this.$router.push("/main/" + key)
    },
    async connectdebug() {
      this.analyLog = "连接中...."
      this.analyse = true
      connectDebugApi().then(res => {
        if (res.data.code === 200 || res.data.code === 300) {
          this.statusFlag = true
          this.analyLog = "分析中..."
          runAnalyApi().then(res => {
            this.waitAnalyseComplete()
          })
        } else {
          this.$message.error(res.data.msg)
          this.analyse = false
        }
      }).catch(err => {
        this.$message.error("内部服务异常，请检查服务端口")
      })
    },

    async waitAnalyseComplete() {
      let complete = false
      while (!complete) {
        let result = await delay(analyStatusApi, 1000)
        if (result.data.msg) {
          this.setTargetList()
          complete = true
          this.analyse = false
        }
      }
    },
    setTargetList() {
      getListDataApi().then(res => {
        let lists = []
        if (res.data.msg.spring) {
          lists.push("spring")
        }
        if (res.data.msg.struts) {
          lists.push("struts")
        }
        if (res.data.msg.tomcat) {
          lists.push("tomcat")
        }
        if (res.data.msg.jetty) {
          lists.push("jetty")
        }
        if (res.data.msg.jersey) {
          lists.push("jersey")
        }
        this.targets = lists;
      })
    },
    cleanData() {
      cleanDataApi()
      this.$router.push("/main")
      this.reload()
    },
    disConnectDebug() {
      stopConnectApi().then(res=>{
        this.reload()
      })
    },
    settingConnectArg(){
      if(this.statusFlag){
        this.$message.error("设置前需要断开连接")
        return
      }else{
        this.dialogVisible = true
      }
    },
    exportData(){
      if(this.targets.length === 0){
        this.$message.error("无数据!")
        return
      }else{
        exportAllApi().then(res=>{
          let blob = new Blob([res.data]);
          let url = window.URL.createObjectURL(blob);
          let a = document.createElement('a');
          a.href = url;
          a.download = 'output.xls';
          a.click();
          window.URL.revokeObjectURL(url);
        })
      }
    },
    emptybutton(){
      this.$message.warning("计划开放")
    }
  },
  created() {
    getArgApi().then(res => {
      if (res.data.code !== 200) {
        this.$message.warning(res.data.msg)
        this.$router.push("/index")
      } else {
        this.ip = res.data.msg.address
        this.port = res.data.msg.port
        this.url = res.data.msg.url
        existDataApi().then(res => {
          if (res.data.code === 200) {
            this.setTargetList()
          }
        })
        this.show = true
      }
    })
    connectStatusApi().then(res => {
      if (res.data.code === 200) {
        this.statusFlag = true
      } else {
        this.statusFlag = false
      }
    })
  },
  components: {
    BugInfo
  }
}
</script>

<style scoped>
.el-header {
  border-radius: 25px;
  box-shadow: 2px 2px 6px #F8F8FF;
  background-color: #FFFFFF;
}

img {
  width: 50px;
  height: 50px;
}

.el-menu-demo {
  margin-right: 10px;
}

.status, .button, .complex {
  margin-top: 20px;
}

.url {
  padding-bottom: 10px;
}
</style>
