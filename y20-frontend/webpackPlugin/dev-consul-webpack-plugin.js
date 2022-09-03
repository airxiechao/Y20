module.exports = class BundleWebpackPlugin {
  constructor(props) {
    this.consul = props.consul
    this.isDev = props.isDev
    this.isHttps = props.isHttps
    this.port = props.port
    this.name = props.name
  }

  apply(compiler) {
    compiler.hooks.done.tapAsync(
      'DevConsulWebpackPlugin',
      (compilation, callback) => {
        const { consul, isDev, isHttps, port, name } = this
        if (isDev) {
          const serviceName = `y20-frontend-${name}-dev`
          consul.agent.service.register({
            name: serviceName,
            address: '127.0.0.1',
            port: port,
            check: {
              http: `${isHttps?'https':'http'}://127.0.0.1:${port}/health`,
              TLSSkipVerify: true,
              interval: '10s',
              timeout: '5s',
            }
          }, function (err, result) {
            if (err) {
              console.error(err);
              throw err;
            }

            console.log(`register [${serviceName}] to consul`);
          })
        }

        callback()
      }
    )
  }
}
