<template>
  <div v-if="exist">
    <el-container>
      <el-header>
        <h2>匹配模式: {{moduleName}}</h2>
      </el-header>
      <el-main>
        <el-table
          :data="tableData"
          style="width: 100%"
          height="400">
          <el-table-column
            prop="api"
            label="接口"
            width="300">
          </el-table-column>
          <el-table-column
            prop="name"
            label="映射类"
            width="600">
          </el-table-column>
        </el-table>
      </el-main>
      <el-footer>
        count: <el-tag type="info">{{count}}</el-tag>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import {existTargetApi, getTargetDataApi, getStrutsModuleName} from "../../utils/dataApi";

export default {
  name: "Struts",
  data() {
    return {
      tableData: [],
      exist: false,
      count: 0,
      moduleName:""
    }
  },
  async created() {
    await existTargetApi("struts").then(res => {
      if (!res.data.msg) {
        this.$message.error("无对应路由")
        this.$router.push("/main")
        this.exist = false
      } else {
        this.exist = true
      }
    })
    if (this.exist) {
      getTargetDataApi('struts').then(res => {
        let mapkey = Object.keys(res.data.msg)
        this.count = mapkey.length
        let tmpList = []
        for(let i=0; i < mapkey.length; i++){
          let data = new Object()
          data.api = mapkey[i]
          data.name = res.data.msg[mapkey[i]]
          tmpList.push(data)
        }
        this.tableData = tmpList
      })

      getStrutsModuleName().then(res=>{
        this.moduleName = res.data.msg
      })
    }
  },
  mounted() {
    const timer = setInterval(()=>{
      getTargetDataApi('struts').then(res => {
        let mapkey = Object.keys(res.data.msg)
        this.count = mapkey.length
        let tmpList = []
        for(let i=0; i < mapkey.length; i++){
          let data = new Object()
          data.api = mapkey[i]
          data.name = res.data.msg[mapkey[i]]
          tmpList.push(data)
        }
        this.tableData = tmpList
      })
    }, 1000)
    this.$once('hook:beforeDestroy',()=>{
      clearInterval(timer)
    })
  }
}
</script>

<style scoped>

</style>
