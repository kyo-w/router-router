const { defineConfig } = require('@vue/cli-service')
const CompressionPlugin = require("compression-webpack-plugin")

module.exports = defineConfig({
  transpileDependencies: true,
  chainWebpack:(config)=>{
    config.plugin('compressionPlugin').use(new CompressionPlugin({
      test: /\.(js|css|less|map)$/, // 匹配文件名
      threshold: 512, // 对超过10k的数据压缩
      minRatio: 0.8,
    }))
  }
})
