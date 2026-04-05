<script setup lang="ts"></script>

<template>
  <div class="page">
    <el-card>
      <template #header>关于我们</template>
      <el-space direction="vertical" alignment="start" :size="12">
        <div>这里可以介绍平台使命、流程、数据公示与联系方式。</div>
        <el-alert title="提示" type="info" :closable="false">
          <template #default>这页是原型占位，后续再补充真实内容。</template>
        </el-alert>
      </el-space>
    </el-card>
  </div>
</template>

<style scoped>
.page {
  padding: 16px;
}
</style>

