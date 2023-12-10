<script setup>

import BaseFramework from "@/components/show/context/BaseFramework.vue";
</script>

<template>
  <BaseFramework name="struts">
    <template #tableList="tableList">
      <el-table :data="tableList.data.handlerChain">
        <el-table-column type="expand">
          <template #default="props">
            <div>
              <h3>interceptor</h3>
              <ul v-for="item in props.row.interceptor">
                <li>{{ item}}</li>
              </ul>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="路由映射" prop="url"/>
        <el-table-column label="调用类" prop="handlerClass"/>
      </el-table>
    </template>
  </BaseFramework>
</template>

<style scoped>

</style>