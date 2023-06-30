<template>
  <div v-loading="loading"
       element-loading-text="未完成分析"
  >
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
            label="接口">
          </el-table-column>
          <el-table-column
            prop="name"
            label="映射类">
          </el-table-column>
        </el-table>
      </el-main>
      <el-footer>
        count:
        <el-tag type="info">{{ count }}</el-tag>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import {existTargetApi, getTargetDataApi, getTargetVersionAip} from "../../utils/dataApi";
import {onBeforeUnmount} from "vue";
import {Message} from "@/utils/message";

export default {
  name: "Jetty",
  data() {
    return {
      tableData: [],
      version: "",
      loading: true,
      count: 0

    }
  },
  async created() {
    await existTargetApi("jetty").then(res => {
      if (!res.data.msg) {
        this.loading = true
      } else {
        this.loading = false
      }
    })
    if (!this.loading) {
      getTargetDataApi('jetty').then(res => {
        let mapkey = Object.keys(res.data.msg)
        this.count = mapkey.length
        let tmpList = []
        for (let i = 0; i < mapkey.length; i++) {
          let data = new Object()
          data.api = mapkey[i]
          data.name = res.data.msg[mapkey[i]]
          tmpList.push(data)
        }
        this.tableData = tmpList
      })
      getTargetVersionAip('jetty').then(res => {
        this.version = res.data.msg
      })
    }
  }, mounted() {
    const timer = setInterval(() => {
      if(!this.loading){
        clearInterval(timer)
        return
      }
      existTargetApi("jetty").then(res => {
        if (!res.data.msg) {
          this.loading = true
        } else {
          this.loading = false
          getTargetDataApi('jetty').then(res => {
            let mapkey = Object.keys(res.data.msg)
            this.count = mapkey.length
            let tmpList = []
            for (let i = 0; i < mapkey.length; i++) {
              let data = new Object()
              data.api = mapkey[i]
              data.name = res.data.msg[mapkey[i]]
              tmpList.push(data)
            }
            this.tableData = tmpList
          })
        }
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
