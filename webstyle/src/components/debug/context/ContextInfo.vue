<script setup lang="ts">
import {ref, watch} from "vue";
import {getContextByName} from "@/api/dataApi";
import {reactive} from "vue";
import {Bottom} from '@element-plus/icons-vue'
import {saveAs} from 'file-saver';

const props = defineProps({
  contextInfo: {
    type: Object,
    required: true,
  }
})
const context = ref()
const render = ref(false)
const tableData: any = reactive([])
const filterData: any = reactive([])
const prefix = ref()
const getVersion = (version: string) => {
  if (version === null) {
    return "unknown"
  }
  return version
}
const getVirtualPath = (path: string) => {
  if (path === '') {
    return '/'
  }
  return path
}
const output = () => {
  let data = ""
  if (prefix.value === null) {
    prefix.value = '/'
  }
  for (let elem of tableData) {
    data += prefix.value + elem.url + "\n"
  }
  let str = new Blob([data], {type: 'text/plain;charset=utf-8'});
  saveAs(str, `api.txt`);
}

watch(props.contextInfo, (value, oldValue) => {
  getContextByName(value.name, value.index).then(res => {
    context.value = res.getContext()
    tableData.length = 0
    filterData.length = 0
    for (let [key, value] of Object.entries(context.value.servletMap)) {
      tableData.push({"url": key, "classname": value})
    }
    for (let elem of context.value.filterMap) {
      filterData.push(elem)
    }
  })
})
getContextByName(props.contextInfo.name, 1).then(res => {
  context.value = res.getContext()
  tableData.length = 0
  filterData.length = 0
  for (let [key, value] of Object.entries(context.value.servletMap)) {
    tableData.push({"url": key, "classname": value})
  }
  for (let elem of context.value.filterMap) {
    filterData.push(elem)
  }
  render.value = true
})
</script>

<template>
  <div v-if="render">
    <el-container class="container">
      <el-header class="header">
        <el-descriptions
            title="基础信息"
            direction="vertical"
            :column="2"
            border
        >
          <template #extra id="group">
            <el-input max="60px"
                      placeholder="前缀拼接模板"
                      v-model="prefix"
            >
              <template #append>
                <el-button type="primary" @click="output">
                  导出路由
                  <el-icon>
                    <Bottom/>
                  </el-icon>
                </el-button>
              </template>
            </el-input>
          </template>
          <el-descriptions-item label="版本">{{ getVersion(context.version) }}</el-descriptions-item>
          <el-descriptions-item label="虚拟根路径">{{ getVirtualPath(context.virtualPath) }}</el-descriptions-item>
          <el-descriptions-item label="物理根目录路径">{{ context.physicalPath }}</el-descriptions-item>
        </el-descriptions>
      </el-header>
      <el-main class="context">
        <el-tabs>
          <el-tab-pane label="urlMap">
            <el-table
                :data="tableData"
                height="300px"
            >
              <el-table-column prop="url" label="路径映射"/>
              <el-table-column prop="classname" label="类名"/>
            </el-table>
            <div>
              <el-statistic class="flat" title="总计个数" :value="tableData.length"/>
            </div>
          </el-tab-pane>
          <el-tab-pane label="filterMap">
            <el-table
                :data="filterData"
                height="300px"
            >
              <el-table-column prop="urlPath" label="路径映射"/>
              <el-table-column prop="className" label="类名"/>
            </el-table>
            <div>
              <el-statistic class="flat" title="总计个数" :value="filterData.length"/>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.container {
  height: 100%;
}

.header {
  height: auto;
}

.context {
  flex: 1;
}

#group {
  float: left;
}
</style>