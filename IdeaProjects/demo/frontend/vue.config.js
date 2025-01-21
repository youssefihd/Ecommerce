const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8082
  },
  proxy: {
    '/api': {
      target: 'http://localhost:5051',
      changeOrigin: true,
    },
});
