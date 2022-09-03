const fs = require('fs')
const path = require('path')

module.exports = class BundleWebpackPlugin {
    constructor (props) {
        const dist = props.dist || '.'
        const bundleName = props.bundleName || 'modules_bundle'
        this.MODUELS_JSON_PATH = path.join(
            __dirname,
            '..',
            dist,
            `${bundleName}.json`
        )
        this.MODULES_DIR = path.dirname(this.MODUELS_JSON_PATH)
        this.IS_DEV = props.isDev
    }

    apply (compiler) {
        compiler.hooks.done.tapAsync(
            'BundleWebpackPlugin',
            (compilation, callback) => {
                const { MODUELS_JSON_PATH, MODULES_DIR, IS_DEV } = this
                const entryNames = Array.from(
                    compilation.compilation.entrypoints.keys()
                )
                const extensionRegexp = /\.(css|js|mjs)(\?|$)/
                const entryPointPublicPathMap = {}
                const modulesMap = {}

                for (let i = 0; i < entryNames.length; i++) {
                    const entryName = entryNames[i]
                    const entryPointFiles = compilation.compilation.entrypoints
                        .get(entryName)
                        .getFiles()
                    const assets = {
                        isDev: IS_DEV,
                        js: '',
                        css: ''
                    }
                    entryPointFiles
                        .map((chunkFile) =>
                            chunkFile
                                .split('/')
                                .map(encodeURIComponent)
                                .join('/')
                        )
                        .map((entryPointPublicPath) => {
                            const extMatch = extensionRegexp.exec(
                                entryPointPublicPath
                            )
                            // Skip if the public path is not a .css, .mjs or .js file
                            if (!extMatch) {
                                return
                            }
                            // Skip if this file is already known
                            // (e.g. because of common chunk optimizations)
                            if (entryPointPublicPathMap[entryPointPublicPath]) {
                                return
                            }
                            entryPointPublicPathMap[entryPointPublicPath] = true
                            // ext will contain .js or .css, because .mjs recognizes as .js
                            const ext = extMatch[1] === 'mjs' ? 'js' : extMatch[1]
                            assets[ext] = entryPointPublicPath
                        })

                    modulesMap[entryName] = assets
                }

                let json = {}
                if (fs.existsSync(MODUELS_JSON_PATH)) {
                    json = JSON.parse(
                        fs.readFileSync(MODUELS_JSON_PATH).toString()
                    )
                }
                json = {
                    ...json,
                    ...modulesMap
                }
                if (!fs.existsSync(MODULES_DIR)) {
                    fs.mkdirSync(MODULES_DIR)
                }
                fs.writeFileSync(MODUELS_JSON_PATH, JSON.stringify(json, null, 2))

                callback()
            }
        )
    }
}
