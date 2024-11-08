<script setup lang="ts">
import BaseFramework from "@/components/debug/context/BaseFramework.vue";
</script>

<template>
  <BaseFramework name="spring">
    <template #tableList="tableList">
      <el-table :data="tableList.data" height="300px">
        <el-table-column label="路由映射" prop="url"/>
        <el-table-column label="调用类" prop="classname"/>
      </el-table>
    </template>
  </BaseFramework>
</template>
<style scoped>
</style>