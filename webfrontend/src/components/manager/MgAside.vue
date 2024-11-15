<template>
  <div>
    <span class="aside-logo">router</span>
    <el-menu
        default-active="2"
        class="el-menu-vertical-demo"
    >
      <el-menu-item index="1" @click="handlerProgress">
        <el-icon>
          <icon-menu/>
        </el-icon>
        <span>任务进度</span>
      </el-menu-item>
      <el-menu-item index="2" @click="handlerData">
        <el-icon>
          <document/>
        </el-icon>
        <span>数据</span>
      </el-menu-item>
      <el-menu-item index="3" @click="handlerProfile">
        <el-icon>
          <User/>
        </el-icon>
        <span>项目参数</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>
<script setup lang="ts">
import {
  Document,
  Menu as IconMenu,
  User,
} from '@element-plus/icons-vue'
import {useRouter} from "vue-router";

const router = useRouter()

const handlerProgress = () => router.push('/mg/progress')
const handlerProfile = () => router.push('/mg/profile')
const handlerData = ()=> router.push('/mg/data')
</script>
<style scoped>
.aside-logo {
  display: block;
  height: 100px;
  line-height: 100px;
  font-size: 24px;
  font-weight: bold;
  margin: auto;
  background-color: rgba(234, 234, 212, 0.98);
  text-align: center;
}
</style>
