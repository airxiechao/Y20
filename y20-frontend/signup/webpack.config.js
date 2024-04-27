const Webpack = require('webpack')
const path = require('path')
const consul = require('consul')()
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
const DevConsulWebpackPlugin = require('../webpackPlugin/dev-consul-webpack-plugin')
const CopyWebpackPlugin = require('copy-webpack-plugin')

const webpackBaseConfig = require('../webpack.base.config')
const portConifg = require('../dev-server-port.config')

module.exports = (env, argv) => {
  const isDev = argv.mode === 'development'
  const name = 'signup'
  const port = portConifg[name]
  const frontendServiceName = name + (isDev ? '-dev' : '')

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
    // new CopyWebpackPlugin({
    //   patterns: [
    //     {
    //         from: path.join(__dirname, './src/assets/static'),
    //         to: `${config.output.path}/static`
    //     }
    //   ]
    // }),
    new Webpack.DefinePlugin({
      FRONTEND_SERVICE_NAME: JSON.stringify(frontendServiceName),
      ENABLE_SIGNUP_MOBILE: env.ENABLE_SIGNUP_MOBILE !== undefined ? env.ENABLE_SIGNUP_MOBILE == 'true' : true,
    }),
  ]
    
  config.externals = {
    'vue': 'Vue'
  }

  return config
}
