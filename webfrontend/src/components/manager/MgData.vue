<template>
  <div v-if="dataType === 'middleware'">
    <middleware-box :middle-list="props.middlewareData"/>
  </div>
  <div v-else-if="dataType ==='framework'">
    <framework-box :framework-list="props.frameworkData"/>
  </div>
  <div v-else></div>
</template>
<script setup lang="ts">
import {Middleware} from "@/api/data";
import MiddlewareBox from "@/components/manager/MiddlewareBox.vue";
import FrameworkBox from "@/components/manager/FrameworkBox.vue";

const props = defineProps({
  dataType: String as () => 'framework' | 'middleware',
  middlewareData: {
    type: Object as () => Middleware[],
    required: true,
    default: () => ([])
  },
  frameworkData: Object
})
</script>
<style scoped>

</style>
