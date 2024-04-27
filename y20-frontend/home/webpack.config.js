const Webpack = require('webpack')
const path = require('path')
const consul = require('consul')()

const HtmlWebpackPlugin = require('html-webpack-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
const DevConsulWebpackPlugin = require('../webpackPlugin/dev-consul-webpack-plugin')
const CopyWebpackPlugin = require('copy-webpack-plugin')
const PrerenderSpaPlugin = require('../webpackPlugin/prerender-spa-plugin')
const Renderer = PrerenderSpaPlugin.PuppeteerRenderer

const webpackBaseConfig = require('../webpack.base.config')
const portConifg = require('../dev-server-port.config')


module.exports = (env, argv) => {
  const isDev = argv.mode === 'development'
  const name = 'home'
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
    new HtmlWebpackPlugin({
      title: '鲲擎运维 Y20',
      description: '鲲擎运维（Y20）是一个CI/CD流水线系统，通过编排流水线实现流程的标准化、自动化！',
      keywords: '鲲擎运维,Y20,持续部署,持续集成,流水线,自动化运维,CI,CD,devops',
      ogTitle: '鲲擎运维 Y20',
      ogImage: 'https://y20.work/icon-logo.png',
      ogUrl: 'https://y20.work',
      template: './src/assets/index.html',
      filename: 'index.html',
      isDev,
    }),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: path.join(__dirname, './src/assets/static'),
          to: `${config.output.path}/static`
        }
      ]
    }),
    new DevConsulWebpackPlugin({
      consul,
      isDev,
      isHttps: true,
      port,
      name,
    }),
    new Webpack.DefinePlugin({
      __VUE_PROD_DEVTOOLS__: 'false',
      __IS_DEV__: isDev?'true':'false', 
      FRONTEND_SERVICE_NAME: JSON.stringify(frontendServiceName),
    }),
    new PrerenderSpaPlugin({
      staticDir: path.resolve(`../dist`),
      indexPath: path.resolve(`../dist/${name}/index.html`),
      outputDir: path.resolve(`../dist/${name}`),
      routes: [ '/' ],
      renderer: new Renderer({
        viewport: { width: 1366, height: 768}
      }),
    }),
  ]
    
  config.externals = {
    'vue': 'Vue'
  }

  return config
}
