<script setup lang="ts">
import Navigation from "@/components/debug/Navigation.vue";
import Theme from "@/components/Theme.vue";
</script>

<template>
  <el-container>
    <Theme/>
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