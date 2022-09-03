import mitt from 'mitt'

const eventBus = window.eventBus = mitt()

function install(app, options){
  app.config.globalProperties.$eventBus = eventBus
  app.provide('eventBus', app.config.globalProperties.$eventBus)
}

export default {
  eventBus,
  install
}