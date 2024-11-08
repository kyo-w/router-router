<script setup lang="ts">
import {reactive, ref, watch} from "vue";
import {getContextByName} from "@/api/dataApi";
import {Bottom} from "@element-plus/icons-vue";
import {saveAs} from "file-saver";

const props = defineProps({
  frameworkInfo: {
    type: Object,
    required: true,
  }
})
const framework = ref()
const urlMap: any = reactive([])
const render = ref(false)
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
  for (let elem of urlMap) {
    data += prefix.value + elem.url + "\n"
  }
  let str = new Blob([data], {type: 'text/plain;charset=utf-8'});
  saveAs(str, `api.txt`);
}

// 监控tab切换
watch(props.frameworkInfo, (value, oldValue) => {
  getContextByName(value.name, value.index).then(res => {
    framework.value = res.getContext()
    urlMap.length = 0
    for (let [key, value] of Object.entries(res.getContext().urlMap)) {
      urlMap.push({"url": key, "classname": value})
    }
  })
})

// 第一次进入初始化
getContextByName(props.frameworkInfo.name, 1).then(res => {
  framework.value = res.getContext()
  urlMap.length = 0
  for (let [key, value] of Object.entries(res.getContext().urlMap)) {
    urlMap.push({"url": key, "classname": value})
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
          <el-descriptions-item label="版本">{{ getVersion(framework.version) }}</el-descriptions-item>
          <el-descriptions-item label="虚拟根路径">{{ getVirtualPath(framework.contextPath) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-header>
      <el-main class="context">
        <slot name="tableList" :data="urlMap"></slot>
        <el-statistic class="flat" title="总计个数" :value="urlMap.length"/>
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
</style>