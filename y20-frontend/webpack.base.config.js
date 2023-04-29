const path = require('path')
const Webpack = require('webpack')

const BundleWebpackPlugin = require('./webpackPlugin/bundle-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const { VueLoaderPlugin } = require('vue-loader')

const { GitRevisionPlugin } = require('git-revision-webpack-plugin')
const gitRevisionPlugin = new GitRevisionPlugin()

const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin

module.exports = ({ entry, publicPath, dist, port = 8080, argv, env }) => {
  const isDev = argv.mode === 'development'
  const envDist = env && env.dist ? env.dist : 'dist'
  const buildDist = path.join(__dirname, envDist, dist)
  return {
    devtool: 'source-map',
    entry,
    output: {
      publicPath,
      path: buildDist,
      filename: '[name].[contenthash].js',
      chunkFilename: '[name].[chunkhash].js',
    },
    module: {
      rules: [
        {
          test: /\.vue$/,
          include: [path.resolve('src')],
          loader: 'vue-loader'
        },
        {
          test: /\.js$/,
          include: [path.resolve('src')],
          use: ['babel-loader']
        },
        {
          test: /\.css$/,
          use: [MiniCssExtractPlugin.loader, 'css-loader']
        },
        {
          test: /\.scss$/,
          use: [MiniCssExtractPlugin.loader, 'css-loader', 'sass-loader']
        },
        {
          test: /\.(png|jpe?g|gif|svg|webp|cur)(\?.*)?$/,
          loader: 'url-loader',
          options: {
            limit: 10000,
            name: '[name].[ext]?[hash]'
          }
        },
        {
          test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
          loader: 'url-loader',
          options: {
            limit: 10000,
            name: '[name].[ext]?[hash]'
          }
        },
        {
          resourceQuery: /blockType=i18n/,
          type: 'javascript/auto',
          loader: '@intlify/vue-i18n-loader'
        }
      ]
    },
    plugins: [
      new VueLoaderPlugin(),
      new BundleWebpackPlugin({
        isDev: isDev,
        dist: envDist,
        bundleName: 'modules_bundle'
      }),
      new Webpack.optimize.LimitChunkCountPlugin({
        maxChunks: 5,
        chunkOverhead: 1000
      }),
      new Webpack.ids.HashedModuleIdsPlugin(),
      new MiniCssExtractPlugin({
        filename: '[name].[contenthash].css',
        chunkFilename: '[name].[chunkhash].css'
      }),
      gitRevisionPlugin,
      new Webpack.DefinePlugin({
        VERSION: JSON.stringify(gitRevisionPlugin.version()),
        COMMITHASH: JSON.stringify(gitRevisionPlugin.commithash()),
        BRANCH: JSON.stringify(gitRevisionPlugin.branch()),
        LASTCOMMITDATETIME: JSON.stringify(gitRevisionPlugin.lastcommitdatetime()),
      }),
      new BundleAnalyzerPlugin({
        analyzerMode: "disabled",
        generateStatsFile: true
      })
    ],
    optimization: {
      chunkIds: 'named',
      realContentHash: true,
      minimize: !isDev
    },
    resolve: {
      extensions: ['.js', '.vue', '.json', '.ts', '.scss', '.css'],
      alias: {
        '@': path.resolve('src'),
      },
      symlinks: false
    },
    devServer: {
      contentBase: path.join(__dirname, envDist),
      historyApiFallback: {
        index: publicPath,
      },
      noInfo: false,
      https: true,
      port,
      before(app, server, compiler) {
        app.get('/health', (req, res) => {
          res.json({
            code: '0',
          })
        })
      },
    }
  }
}
