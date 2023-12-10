<script setup>
import {reactive, ref} from "vue";
import {getContextByName} from "@/utils/DataApi";
import ContextHeader from "@/components/show/context/ContextHeader.vue";
import ContextTable from "@/components/show/context/ContextTable.vue";

const props = defineProps({
  name: {
    type: String,
    required: true
  }
})
const context = reactive({list: []})
const setup = () => {
  getContextByName(props.name).then(res => {
    for (let elem of res.data.msg) {
      context.list.push(elem)
    }
    console.log(context.list)
  })
}

const defaultProps = {
  children: 'subFiles',
  label: 'fileName',
}
const activeName = ref(props.name + '-0')
const handlerClick = (key) => {
  activeName.value = key.props.name
}
setup()
</script>

<template>
  <el-tabs v-model="activeName" @tab-click="handlerClick" class="tabs" type="border-card">
    <el-tab-pane v-for="(elem, key) in context.list" :label="elem.from + '-' + key" :name="elem.from + '-' + key">
      <el-container class="container">
        <el-header class="header">
          <ContextHeader :context="elem"/>
        </el-header>
        <el-main class="context">
          <ContextTable :context="elem"/>
        </el-main>
      </el-container>
    </el-tab-pane>
  </el-tabs>
</template>

<style scoped>
.container {
  height: 100%;
}

.header {
  height: auto;
}

.context {
  flex: 1;
}
</style>