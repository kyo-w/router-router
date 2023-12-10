<script setup>
import Navigation from "@/components/aside/Navigation.vue";
</script>

<template>
  <el-container>
    <el-aside width="200px">
      <Navigation/>
    </el-aside>
    <el-container>
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>

</style>