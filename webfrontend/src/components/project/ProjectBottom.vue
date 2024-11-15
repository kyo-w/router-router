<template>
  <div class="project-bottom-button">
    <div class="box-left" v-if="!props.project?.isEmpty()">
      <el-button type="danger" @click="handlerDeleteProject">删除项目</el-button>
      <el-button type="primary" @click="handlerSelectProject">选择</el-button>
    </div>
    <div class="box-right" v-if="props.project?.isEmpty()">
      <el-button type="primary" @click="openProjectWindow">新建项目</el-button>
      <create-project-item ref="projectWindow"/>
    </div>
  </div>
</template>
<script setup lang="ts">
import {apiDeleteProject, apiSelectProject, Project} from "@/api/project"
import CreateProjectItem from "@/components/project/CreateProjectItem.vue"
import {ref} from "vue"
import {useRouter} from 'vue-router'

const router = useRouter()
const props = defineProps({
  project: Project
})
const emit = defineEmits(['close-handler'])

const handlerSelectProject = () => {
  apiSelectProject(props.project.id).then(res => router.push('/mg/progress'))
}

const projectWindow = ref()
const openProjectWindow = () => {
  projectWindow.value.openCreateProjectWindow()
}
const handlerDeleteProject = () => {
  apiDeleteProject(props.project.id).then(res=>{
    emit('close-handler', props.project.id)
  })
}
</script>

<style scoped>
.project-bottom-button {
  width: 800px;
}

.box-left {
  float: left;
}

.box-right {
  float: right;
}
</style>
