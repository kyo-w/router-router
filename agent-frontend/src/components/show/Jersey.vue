<script setup>

import BaseFramework from "@/components/show/context/BaseFramework.vue";
</script>

<template>
  <BaseFramework name="jersey">
    <template #tableList="tableList">
      <el-table :data="tableList.data.handlerChain">
        <el-table-column label="路由映射" prop="url"/>
        <el-table-column label="调用类" prop="handlerClass"/>
      </el-table>
    </template>
  </BaseFramework>
</template>

<style scoped>

</style>