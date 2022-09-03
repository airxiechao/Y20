function isObject (o) {
  return o !== null && typeof o === 'object' && !Array.isArray(o)
}

const mergeModules = (target, ...modules) => {
  return modules.reduce((merged, mod) => {
      Object.keys(mod).map(key => {
          if (isObject(merged[key]) && isObject(mod[key])) {
              merged[key] = {
                  ...merged[key],
                  ...mod[key]
              }
          }
      })

      return merged
  }, target)
}

export default {
  mergeModules
}