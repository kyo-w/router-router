<script setup lang="ts">
import {getCurrentInstance} from "vue";

const changeTheme = getCurrentInstance().appContext.config.globalProperties.changeTheme

</script>

<template>
  <div @click="changeTheme" class="my_theme">
    <el-icon size="30">
      <Sunny/>
    </el-icon>
  </div>
</template>

<style scoped>
.my_theme{
  position: absolute;
  width: 50px;
  height: 50px;
  top: 0;
  right: 0;
}
</style>