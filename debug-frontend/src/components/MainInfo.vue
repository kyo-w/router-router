<template>
  <el-row :gutter="10" class="top_collection">
    <el-col :span="10">
      <div>
        <el-card class="box-card">
          <div class="info_div">
            <img class="info_img" src="@/assets/IP.png" alt="your-image">
            <span class="text">{{ this.ip }}</span>
          </div>
          <div class="info_div">
            <img class="info_img" src="@/assets/port.png" alt="your-image">
            <span class="text">{{ this.port }}</span>
          </div>
        </el-card>
      </div>
    </el-col>
    <el-col :span="14">
      <div>
        <el-card class="box-card">
          <el-switch
              class="status"
              v-model="statusFlag"
              inline-prompt
              active-text="连接"
              inactive-text="未连接"
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              :before-change="beforeChange1"
          />
          <el-button type="primary" @click="connectDebug">连接分析</el-button>
          <el-button type="primary" @click="settingConnectArg">设置</el-button>
          <el-button type="primary" @click="connectLogDrawer = true">连接日志</el-button>
          <el-button type="primary" @click="startAnalysts">继续调试断点</el-button>
          <el-switch
              class="status"
              v-model="debugStatus"
              inline-prompt
              active-text="调试中"
              inactive-text="已停止调试"
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              :before-change="beforeChange1"
          />
          <hr/>
          <el-button type="danger" @click="emptyButton">未授权探测</el-button>
          <el-button type="danger" @click="stopAnalysts">停止调试断点</el-button>
          <el-button type="danger" @click="cleanData">置空数据</el-button>
          <el-button type="danger" @click="exportData">一键导出(xls)</el-button>
        </el-card>
      </div>
    </el-col>
  </el-row>
  <el-row :gutter="10">
    <el-col :span="4">
      <div style="height: 500px">
        <el-menu class="el-menu-demo" mode="el-menu-vertical-demo" @select="handleSelect" v-for="item in targets">
          <el-menu-item :index="item"><img :src="'/static/' + item + '.png'">{{ item }}</el-menu-item>
        </el-menu>
      </div>
    </el-col>
    <el-col :span="20">
      <div class="grid-content ep-bg-purple" style="height: 500px">
        <router-view/>
      </div>
    </el-col>
  </el-row>
  <el-drawer
      v-model="connectLogDrawer"
      size="40%"
      title="连接日志">
    <div class="log_content_box">
      <p v-for="item in logInfo" :class="['log_content', item.debugType]">
        {{ "[" + item.debugType + "]: " + item.debugMessage }}</p>
    </div>
  </el-drawer>
  <el-dialog
      v-model="dialogVisible"
      width="40%">
    <bug-info/>
  </el-dialog>
</template>

<script>
import {
  connectDebugApi,
  cleanDataApi,
  connectStatusApi,
  analystsStop,
  analystsStart,analyStatusApi
} from "../utils/manageApi";
import {getArgApi} from "../utils/settingApi";
import { getListDataApi, exportAllApi} from "../utils/dataApi";

import BugInfo from './BugInfo.vue';
import Constant from "@/utils/constant";
import {onBeforeUnmount} from "vue";
import {reactive} from 'vue'
import {Message} from "@/utils/message";

export default {
  name: "MainInfo",
  inject: ['reload'],
  data() {
    return {
      ip: "127.0.0.1",
      port: "5005",
      url: "",
      statusFlag: false,
      targets: reactive({
        list: []
      }),
      debugStatus: false,
      dialogVisible: false,
      connectLogDrawer: false,
      logInfo: [],
      socket: null
    }
  },
  methods: {
    handleSelect(key, keyPath) {
      this.$router.push("/main/" + key)
    },
    connectDebug() {
      connectDebugApi().then(res => {
        if (res.data.code === 200) {
          this.statusFlag = true
          this.debugStatus = true
          Message('消息', res.data.msg, 'info')
        } else if (res.data.code === 300) {
          Message('消息', res.data.msg, 'warning')
        }else if(res.data.code === 400){
          Message('消息', res.data.msg, 'error')
        }
      }).catch(err => {
        Message('网络异常', '内部服务异常，检查服务是否正常运行', 'error')
      })
      this.connectLogDrawer = true
    },
    cleanData() {
      cleanDataApi()
      this.$router.push("/main")
      this.reload()
    },
    settingConnectArg() {
      if (this.statusFlag) {
        Message('功能使用错误', '设置前需要断开连接', 'error')
        return
      } else {
        console.log(111)
        this.dialogVisible = true
      }
    },
    exportData() {
      if (this.targets.length === 0) {
        Message('数据', '无数据', 'error')
        return
      } else {
        exportAllApi().then(res => {
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
    emptyButton() {
      Message('实验性功能', '', 'warning')
    },
    beforeChange1() {
      return false
    },
    getListData() {
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

        console.log(lists)
        this.targets = lists;
      })
    },
    stopAnalysts() {
      analystsStop().then(res=>{
        this.debugStatus = false
        Message('信息', res.data.msg, 'info')
      })
    },
    startAnalysts(){
      if(!this.statusFlag){
        Message('信息', '必须先连接成功!', 'warning')
        return
      }
      analystsStart().then(res=>{
        this.debugStatus = true
        Message('信息', res.data.msg, 'info')
      })
    }
  },
  created() {
    this.socket = new WebSocket(Constant.WebsocketLocation)
    getArgApi().then(res => {
      if (res.data.code !== 200) {
        Message('警告', res.data.msg, 'warning')
        this.$router.push("/index")
      } else {
        this.ip = res.data.msg.hostname
        this.port = res.data.msg.port
        this.url = res.data.msg.url
      }
    })
    this.getListData()
    connectStatusApi().then(res => {
      if (res.data.code === 200) {
        this.statusFlag = true
      } else {
        this.statusFlag = false
      }
    })
    connectStatusApi().then(res => {
      if (res.data.code === 200) {
        this.statusFlag = true
      }
    })
    analyStatusApi().then(res=>{
      if(res.data.msg === true){
        this.debugStatus = false
      }else{
        this.debugStatus = true
      }
    })
  },
  mounted() {
    this.socket.onopen = () => {
      console.log("完成日志连接")
    }
    this.socket.onmessage = (event) => {
      let info = JSON.parse(event.data)
      if (info.debugType === 'Success') {
        this.getListData()
      }
      this.logInfo.push(JSON.parse(event.data))
    };
    onBeforeUnmount(() => {
      this.socket.close()
      console.log("退出日志连接")
    });
  },
  components: {
    BugInfo
  }
}
</script>

<style scoped>

info_div {
  display: flex;
  align-items: center;
}

.info_img {
  display: inline-block;
  vertical-align: middle;
  width: 40px;
  height: 40px;
  padding-right: 20px;
}

.text {
  display: inline-block;
  vertical-align: middle;
  font-size: 20px;
}

.status {
  padding-left: 20px;
  padding-right: 20px;
}

.log_content_box {
  border-radius: 10px 10px 10px 10px;
  background-color: gainsboro;
}

.log_content {
  padding-left: 5px
}


img {
  width: 50px;
  height: 50px;
}

.top_collection {
  padding-bottom: 10px;
}

.Info {
  color: skyblue;
}

.Success {
  color: #13ce66;
}

.Fail {
  color: #ff4949;
}
</style>
