const path = require('path')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')

const webpackBaseConfig = require('../webpack.base.config')
const port = require('../dev-server-port.config')

module.exports = (env, argv) => {
  const config = webpackBaseConfig({
      env,
      argv,
      entry: {
          common: './src/index'
      },
      publicPath: '/common/',
      dist: '/common',
      port: port.common
  })

  config.output = {
    ...config.output,
    path: path.join(__dirname, "dist"),
    filename: '[name].js',
    chunkFilename: '[name].js',
    libraryTarget: 'umd'
  }

  let plugins = []
  config.plugins.forEach(plugin => {
    if(!(plugin instanceof MiniCssExtractPlugin)){
      plugins.push(plugin)
    }
  })

  config.plugins = [
    ...plugins,
    new MiniCssExtractPlugin({
      filename: '[name].css',
      chunkFilename: '[name].css'
    }),
    new HtmlWebpackPlugin({
      template: './src/assets/index.html',
      filename: 'index.html'
    }),
    new CleanWebpackPlugin(),
  ]
    
  config.externals = {
    vue: {
      root: 'Vue',
      commonjs: 'vue',
      commonjs2: 'vue',
    }
  }

  config.devServer.proxy = {
    '/manmachinetest/api': 'http://localhost'
  }

  return config
}
