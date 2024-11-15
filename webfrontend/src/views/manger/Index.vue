<template>
  <el-container>
    <el-aside width="200px" class="aside">
      <mg-aside/>
    </el-aside>
    <el-container>
      <el-header height="100px">
        <mg-header class="container-mg-header"/>
      </el-header>
      <el-main class="container-main">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>
<script setup lang="ts">

import MgAside from "@/components/manager/MgAside.vue";
import MgHeader from "@/components/manager/MgHeader.vue";
</script>
<style scoped>
.container-mg-header {
  border-image-source: radial-gradient(60% 60%, transparent 0px, transparent 100%, #d2c1c1 100%);
  border-image-slice: 1;
  border-width: 1px;
  border-style: solid;
}

.container-main {
  margin-top: 10px;
}

.aside {
  padding: 0;
  margin: 0;
}
</style>
