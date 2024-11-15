<template>
  <el-container>
    <el-header>
      <el-image :src="require('@/assets/logo.png')"/>
    </el-header>
    <el-main>
      <div class="main">
        <project-title :project="selectProject"/>
        <el-collapse @change="handlerProject" accordion>
          <el-collapse-item v-for="(project, index) in projectLists" :name="index">
            <template #title>
              <project-name-icon :show="selectProject === project && !selectProject.isEmpty()" :project="project"/>
            </template>
            <project-profiler :project="project"/>
          </el-collapse-item>
        </el-collapse>
        <project-bottom class="bottom-element" :project="selectProject" @close-handler="clearProject"/>
      </div>
    </el-main>
  </el-container>
</template>
<script setup lang="ts">
import {apiGetAllProject, Project} from "@/api/project"
import {ref} from "vue"
import ProjectProfiler from "@/components/project/ProjectProfiler.vue";
import ProjectNameIcon from "@/components/project/ProjectNameIcon.vue";
import ProjectTitle from "@/components/project/ProjectTitle.vue";
import ProjectBottom from "@/components/project/ProjectBottom.vue";

const projectLists = ref<Project[]>([])
const selectProject = ref<Project>(Project.default)
apiGetAllProject().then(res => {
  projectLists.value = res
})
const handlerProject = function (val: any) {
  if (typeof val === "string") {
    selectProject.value = Project.default
  } else {
    selectProject.value = projectLists.value[val]
  }
}

const clearProject = function (id: string) {
  projectLists.value = projectLists.value.filter(item => item.id !== id)
  selectProject.value = Project.default
}

</script>
<style scoped>
.main {
  background-color: #fff;
  border-radius: 20px;
  width: 800px;
  height: 600px;
  margin: auto;
  position: relative;
  border: 1px solid #a7a7b7;
  padding-top: 20px;
  padding-left: 20px;
  padding-right: 20px;
  box-shadow: rgba(0, 0, 0, 0.15) 2.4px 2.4px 3.2px;
}

.bottom-element {
  position: absolute;
  bottom: 20px;
}


</style>
