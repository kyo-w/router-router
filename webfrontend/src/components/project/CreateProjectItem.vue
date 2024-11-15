<template>
  <el-dialog v-model="openStatus" title="项目参数" width="500" draggable>
    <el-form :inline="true" :model="project" label-width="auto" :rules="rules">
      <el-form-item label="项目名" label-position="right" prop="name">
        <el-input v-model="project.name" placeholder="" clearable/>
      </el-form-item>
      <el-form-item label="主机" label-position="right" prop="hostname">
        <el-input v-model="project.hostname" placeholder="IP或者域名" clearable/>
      </el-form-item>
      <el-form-item label="端口" label-position="right" prop="port">
        <el-input v-model="project.port" placeholder="0-65535" clearable/>
      </el-form-item>
    </el-form>
    <el-button type="danger">关闭</el-button>
    <el-button type="primary" @click="sendRequestToCreateProject">创建</el-button>
  </el-dialog>
</template>
<script setup lang="ts">
import {ref} from "vue"
import {apiCreateProject, Project} from "@/api/project"
import {useRouter} from 'vue-router'

const router = useRouter()
const rules = ref({
  hostname: [{
    required: true,
    pattern: /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)+([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$/,
    message: '不正确的主机名',
    trigger: 'blur'
  }],
})
const openStatus = ref(false)
const openCreateProjectWindow = () => {
  openStatus.value = true
}
defineExpose({openCreateProjectWindow})

const project = ref<Project>(Project.default)
const sendRequestToCreateProject = () => {
  project.value.createTime = new Date()
  apiCreateProject(project.value).then(success => {
    if (success) {
      router.push('/mg/progress')
    }
  })
}

</script>
<style scoped>

</style>
