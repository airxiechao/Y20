const consul = require('consul')()
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
const DevConsulWebpackPlugin = require('../webpackPlugin/dev-consul-webpack-plugin')

const webpackBaseConfig = require('../webpack.base.config')
const portConifg = require('../dev-server-port.config')

module.exports = (env, argv) => {
  const isDev = argv.mode === 'development'
  const name = 'user-billing'
  const port = portConifg[name]

  const config = webpackBaseConfig({
      env,
      argv,
      entry: {
        [name]: './src/index'
      },
      publicPath: `/${name}${isDev?'-dev':''}/`,
      dist: `${name}`,
      port: port,
  })
  
  config.plugins = [
    ...config.plugins,
    new CleanWebpackPlugin(),
    new DevConsulWebpackPlugin({
      consul,
      isDev,
      isHttps: true,
      port,
      name,
    }),
  ]
    
  config.externals = {
    'vue': 'Vue'
  }

  return config
}
