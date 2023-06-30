<template>
  <div v-loading="loading"
       element-loading-text="未分析或者分析中..."
  >
    <el-container>
      <el-header>
        <h2 class="tips">出现的顺序非乱序!</h2>
      </el-header>
      <el-main>
        <el-table
            :data="tableData"
            style="width: 100%"
            height="400">
          <el-table-column
              prop="webapp"
              label="webapp上下文">
          </el-table-column>
          <el-table-column
              prop="url"
              label="url">
          </el-table-column>
          <el-table-column
              prop="className"
              label="className">
          </el-table-column>
        </el-table>
      </el-main>
            <el-footer>
              count: <el-tag type="info">{{this.tableData.length}}</el-tag>
            </el-footer>
    </el-container>
  </div>
</template>

<script>
import {getFilterMap} from "@/utils/dataApi";
import {existTargetApi} from "@/utils/dataApi";

export default {
  name: "Filter",
  data() {
    return {
      loading: false,
      tableData: [
        {api: "/test", name: "1"},
        {api: "/test", name: "2"},
        {api: "/test", name: "3"},
        {api: "/test1", name: "4"},
        {api: "/test1", name: "5"},
        {api: "/test1", name: "6"}
      ]
    }
  },
  created: async function () {
    await existTargetApi("filter").then(res => {
      if (!res.data.msg) {
        this.loading = true
      } else {
        this.loading = false
      }
    })
    if (!this.loading) {
      getFilterMap().then(res => {
        let mapkey = Object.keys(res.data.msg)
        let tmpList = []
        for (let elem of mapkey) {
          let NewElem;
          if (elem == "") {
            NewElem = "默认路径(\"\")"
          } else {
            NewElem = elem
          }

          let classNameKey = Object.keys(res.data.msg[elem])
          for (let classElem of classNameKey) {
            for(let urlElem of  res.data.msg[elem][classElem]){
              tmpList.push({webapp: NewElem, className: classElem, url: urlElem})
            }
          }
        }
        this.tableData = tmpList
        console.log(this.tableData)
      })
    }
  },
  methods: {}
}
</script>

<style scoped>
  .tips{
    color: #ff4949;
  }
</style>