<template>
  <div v-if="exist">
    <el-container>
      <el-header>
        <h2>Version: {{ version }}</h2>
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
import {existTargetApi, getTargetDataApi, getTargetVersionAip} from "../../utils/dataApi";
import {Message} from "@/utils/message";
import {onBeforeUnmount} from "vue";

export default {
  name: "Spring",
  data() {
    return {
      tableData: [],
      version: "",
      exist: false,
      count: 0

    }
  },
  async created() {
    await existTargetApi("spring").then(res => {
      if (!res.data.msg) {
        Message('路由', 'Spring路由获取失败', 'error')
        this.$router.push("/main")
        this.exist = false
      }else {
        this.exist = true
      }
    })
    if (this.exist) {
      getTargetDataApi('spring').then(res => {
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
      getTargetVersionAip('spring').then(res => {
        this.version = res.data.msg
      })
    }
  },
  mounted() {
    const timer = setInterval(()=>{
      getTargetDataApi('spring').then(res => {
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
    onBeforeUnmount(() => {
      clearInterval(timer)
    });
  }
}
</script>

<style scoped>

</style>
