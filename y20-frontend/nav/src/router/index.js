import axios from 'axios'
import { createRouter, createWebHistory } from 'vue-router'
import Workspace from '../views/Workspace'
import Project from '../views/Project'
import User from '../views/User'
import Page from '../views/Page'
import Error from '../views/Error'
import Refresh from '../views/Refresh'
import store from '../store'
import { LoadingBar } from 'quasar'

const MODULES_BUNDLE_JSON_PATH = "/modules_bundle.json"

const SECONDARY_NAV = ['workspace', 'project', 'user']
const BYPASS_LOGIN_PATH = [/^\/signup(\/success)?$/, /^\/login(\/twofactor)?$/, /^\/workspace\/template(\/[0-9]+)?(?!.*(apply|my))/]

const routes = [
  {
    path: '/',
    name: 'root',
    component: Page,
    redirect: '/workspace/project',
    children: [
    ],
  }, 
  {
    path: '/workspace',
    name: 'workspace',
    redirect: '/workspace/project',
    component: Workspace,
  }, 
  {
    path: '/project/:projectId',
    name: 'project',
    component: Project,
  },
  {
    path: '/user',
    name: 'user',
    component: User,
  },
  {
    path: '/refresh',
    name: 'refresh',
    component: Refresh,
  }, 
]

let dynamicModules = null
const router = window.router = createRouter({
  history: createWebHistory('/nav'+(__IS_DEV__?'-dev':'')),
  routes
})

function getModuleName(path){
  let pathTokens = path.split('/')
  let name = pathTokens[1]
  if(name == 'workspace'){
    name += '-' + pathTokens[2]
  }else if(name == 'project'){
    name += '-' + pathTokens[3]
  }else if(name == 'user'){
    name += '-' + pathTokens[2]
  }
  return name
}

function getParentRoute(path){
  let routeParent = 'root'
  let name = path.split('/')[1]
  if(SECONDARY_NAV.includes(name)){
    routeParent = name
  }
  return routeParent
}

// 动态获取json
function loadJson(path){
  return axios.get(`${path}?t=${new Date().getTime()}`)
}

// 动态加载js
function importScript (src, targetEle) {
  return new Promise((resolve, reject) => {
      const scriptEle = document.createElement('script')
      scriptEle.type = 'text\/javascript'
      scriptEle.setAttribute('src', src)
      targetEle.appendChild(scriptEle)

      scriptEle.onload = resolve
      scriptEle.onerror = reject
  })
}

// 动态加载css
function importStyle (href, targetEle) {
  return new Promise((resolve, reject) => {
      const styleEle = document.createElement('link')
      styleEle.setAttribute('rel', 'stylesheet')
      styleEle.setAttribute('type', 'text/css')
      styleEle.setAttribute('href', href)
      targetEle.appendChild(styleEle)

      styleEle.onload = resolve
      styleEle.onerror = reject
  })
}

// 动态加载路由
function loadRoute(path){
  const name = getModuleName(path)

  if(!dynamicModules[name]){
    return Promise.reject()
  }

  let isDev = dynamicModules[name]['isDev']
  let css = dynamicModules[name]['css']
  let js = dynamicModules[name]['js']

  return Promise.all([
    //import(/* webpackIgnore: true */ `/${name}/${dynamicModules[name]['js']}`)，
    importStyle(`/${name}${isDev?'-dev':''}/${css}`, document.head),
    importScript(`/${name}${isDev?'-dev':''}/${js}`, document.body),
  ]).then(() => {
    const module = window.Pages[name]
    store.registerModule(name.split('-'), module.store)
    
    let routeParent = getParentRoute(path)
    module.routes.forEach(r => {
      router.addRoute(routeParent, r)
    })
  })
}

router.beforeEach((to, from, next) => {
  
  // check login
  let bypassLogin = false
  BYPASS_LOGIN_PATH.forEach(p => {
    if(p.test(to.path)){
      bypassLogin = true
    }
  })
  if(!bypassLogin){
    if(!store.getters.username){
      store.commit('setPathBeforeLogin', { ...to })
      next('/login')
      return
    }
  }

  if(to.matched.length > 0){
    next()
    return
  }
  
  let loadRoutePromise = Promise.resolve()
  if(!dynamicModules){
    loadRoutePromise = loadRoutePromise.then(() => loadJson(MODULES_BUNDLE_JSON_PATH).then((resp) => {
      let bundle = resp.data
      dynamicModules = bundle
    }))
  }

  loadRoutePromise = loadRoutePromise.then(() => {
    LoadingBar.start()
    return loadRoute(to.path)
  })

  loadRoutePromise = loadRoutePromise.then(() => {
    if(router.resolve(to.path).matched.length > 0) {
      next({
        ...to,
      })
    }else{
      throw "resolve error"
    }
  }).catch((e)=>{
    console.error(`no route for ${to.path}`, e)

    // 替换为错误页
    const errorRoute = {
      path: to.path,
      component: Error,
    }

    let routeParent = getParentRoute(to.path)
    router.addRoute(routeParent, errorRoute)

    next(to.path)
  }).finally(() => {
    LoadingBar.stop()
  })
})

export default router
