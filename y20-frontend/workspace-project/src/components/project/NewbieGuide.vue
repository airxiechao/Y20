<template>
  <q-dialog v-model="flagShowDialog">
    <q-card class="full-width">
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6">新手指引</div>
        <q-space />
        <q-btn icon="close" flat round dense v-close-popup />
      </q-card-section>
      <q-card-section class="q-pa-none">
        <q-stepper
          flat
          v-model="step"
          :vertical="$q.screen.lt.sm"
          color="primary"
        >
          <q-step
            :name="1"
            title="创建项目"
            icon="create_new_folder"
            :done="step > 1"
            :header-nav="step > 1"
          >
            <div class="q-pb-md">
              1. 输入项目名称
            </div>
            <q-form
              @submit="onSubmitCreateProject"
              class="q-gutter-md"
            >
              <q-input
                outlined
                v-model="projectName"
                :label="`${$t('label-project-name')} *`"
                :hint="$t('error-project-name-too-long')"
                lazy-rules
                :rules="[ 
                    val => val && val.length > 0 || $t('error-no-project-name'),
                    val => val.length < 20 || $t('error-project-name-too-long')
                ]"
              />

              <q-stepper-navigation>
                <q-btn unelevated type="submit" class="q-mr-sm" color="primary" label="创建" :loading="createProjectLoading" />
              </q-stepper-navigation>
            </q-form>
          </q-step>

          <q-step
            :name="2"
            title="接入节点"
            icon="computer"
            :done="step > 2"
            :header-nav="step > 2"
          >
            <div class="q-pb-md">
              2. 输入节点名称
            </div>
            <q-form
              @submit="onSubmitCreateAgentJoinScript"
              class="q-gutter-lg q-mb-sm"
            >
              <q-input
                outlined
                v-model="agentId"
                label="节点名称 *"
                hint="自定义的节点唯一标识"
                lazy-rules
                :rules="[ val => val && val.length > 0 || '请输入节点唯一标识']"
              />

              <q-select
                outlined
                v-model="agentOsType"
                :options="[
                  {
                    label: 'Windows', value: 'WINDOWS',
                  },
                  {
                    label: 'Linux', value: 'LINUX',
                  },
                ]" 
                label="节点的操作系统 *"
                hint="支持64位的 Windows 和 Linux。Linux依赖 systemd, unzip"
                emit-value 
                map-options
                lazy-rules
                :rules="[ val => !!val || '请选择节点的操作系统']"
              />

              <div>
                <q-btn unelevated class="q-mr-sm" label="下载接入脚本" type="submit" color="primary" :loading="createAgentJoinScriptLoading" />
                <q-btn flat color="primary" label="下一步" @click="forward" :disable="!flagAgentJoined" />
              </div>

            </q-form>

            <div class="q-mt-sm">
              <div class="text-warning q-py-xs">
                <q-icon name="warning" />
                注意：本地打开 <span class="text-bold">{{agentJoinScriptType}}</span> 终端，切换到安装目录，运行脚本。
                <span v-if="agentJoinScriptType == 'Powershell'">如果安全策略不允许脚本运行，请先以管理员身份打开 <span class="text-bold">{{agentJoinScriptType}}</span> 终端，执行 <span class="text-bold text-grey">set-executionpolicy bypass</span>，更改脚本运行策略</span>
              </div>
              <!-- <q-input
                stack-label
                v-model="agentJoinScript"
                label="接入脚本"
                :hint="`本地打开 ${agentJoinScriptType} 终端，切换到安装目录，运行脚本，完成接入`"
                outlined
                type="textarea"
              /> -->
            </div>

            <q-stepper-navigation v-if="flagAgentWaiting">
              <q-card flat class="text-center q-pa-sm bg-grey-1">
                <div>
                  <q-spinner-facebook color="primary" />
                </div>
                <div>
                  <span class="vertical-middle text-grey">等待节点接入</span>
                </div>
              </q-card>
            </q-stepper-navigation>

            <q-stepper-navigation v-if="flagAgentJoined">
              <q-card flat class="text-center q-pa-sm bg-grey-1">
                <div>
                  <q-icon color="green" name="check_circle" />
                </div>
                <div>
                  <span class="vertical-middle text-grey">节点已接入</span>
                </div>
              </q-card>
            </q-stepper-navigation>

          </q-step>

          <q-step
            :name="3"
            title="启动流水线"
            icon="play_arrow"
            :header-nav="step > 3"
          >
            <div class="">
              3. 点击“启动”。运行 <router-link target="_blank" :to="`/project/${projectId}/pipeline/${pipelineId}/step`">Hello World</router-link> 流水线，观察日志是否输出了 “Hello World”。
            </div>

            <q-stepper-navigation>
              <q-btn unelevated color="primary" @click="onSubmitCreateRun" label="启动" :loading="createRunLoading" />
            </q-stepper-navigation>
          </q-step>
        </q-stepper>
      </q-card-section>
    </q-card>
  </q-dialog>

  <q-card flat class="newbie-guide q-pa-md q-mx-xs q-mb-sm">
    <q-btn class="absolute-top-right cursor-pointer q-ma-sm" icon="close" flat round dense @click="onClickClose" />
    <div class="self-center text-center fig-welcome">
      <img alt="fig-welcome" :src="`/${FRONTEND_SERVICE_NAME}/static/img/fig-welcome.png`"/>
    </div>
    <div class="text-center text-h6">欢迎使用 鲲擎流水线</div>
    <div class="text-center q-mt-sm text-grey">跟随指引，完成第一条 Hello World 流水线的运行！</div>
    <div class="text-center q-mt-md">
      <q-btn unelevated class="q-mr-sm" color="primary" label="开始学习"  @click="onClickBegin" />
    </div>
  </q-card>
</template>

<style lang="scss">
.newbie-guide{
  .fig-welcome{
    img{
      height: 150px;
    }
  }

  .q-stepper__header--border{
    border-bottom: 0;
  }
}
</style>

<i18n>
{
  "zhCN": {
    "label-project-name": "项目名称",
    "error-no-project-name": "请输入项目名称",
    "error-project-name-too-long": "项目名称长度不超过20个字符",
  }
}
</i18n>

<script>
import dayjs from 'dayjs'
import { copyToClipboard } from 'quasar'
import { ref, inject, computed } from 'vue'
import projectApi from '@/api/project.api'
import pipelineApi from '@/api/pipeline.api'
import authApi from '@/api/auth.api'
import agentApi from '@/api/agent.api'

export default {
  setup(props, { emit }){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const step = ref(0)
    const flagShowDialog = ref(false)

    const projectName = ref('新手项目')
    const projectId = ref(null)
    const pipelineId = ref(null)
    const createProjectLoading = ref(false)

    const agentId = ref(null)
    const agentAccessToken = ref('')
    const agentServerHost = ref(window.location.hostname)
    const agentServerRpcPort = ref(9100)
    const agentServerRestPort = ref(window.location.port || (window.location.protocol.startsWith('https') ? '443' : '80'))
    const agentServerRestUseSsl = ref(window.location.protocol.startsWith('https'))
    const agentDataDir = ref('.')
    const agentOsType = ref('WINDOWS')
    const agentJoinScript = ref('')
    const createAgentJoinScriptLoading = ref(false)
    const flagAgentJoined = ref(false)
    const flagAgentWaiting = ref(false)
    let agentWaitingTimer = null

    const createRunLoading = ref(false)

    const agentJoinScriptType = computed(() => {
      switch(agentOsType.value){
        case 'WINDOWS':
          return 'Powershell'
        case 'LINUX':
          return 'Bash'
        default:
          return ''
      }
    })

    function download(fileName, text, hasBom) {
      const url = window.URL || window.webkitURL || window;
      const blob = new Blob((hasBom ? ["\ufeff", text] : [text]), {type: 'text/plain;charset=utf-8'});
      const saveLink = document.createElementNS('http://www.w3.org/1999/xhtml', 'a');
      saveLink.href = url.createObjectURL(blob);
      saveLink.download = fileName;
      saveLink.click();
    }

    return {
      FRONTEND_SERVICE_NAME: FRONTEND_SERVICE_NAME,

      $q,
      step,
      flagShowDialog,

      projectName,
      projectId,
      pipelineId,
      createProjectLoading,

      agentId,
      agentAccessToken,
      agentServerHost,
      agentServerRpcPort,
      agentServerRestPort,
      agentServerRestUseSsl,
      agentDataDir,
      agentOsType,
      agentJoinScriptType,
      agentJoinScript,
      createAgentJoinScriptLoading,
      flagAgentJoined,
      flagAgentWaiting,

      createRunLoading,

      onClickClose(){
        emit('close')
      },

      onClickBegin(){
        step.value = 1
        flagShowDialog.value = true
      },

      forward(){
        step.value += 1
        
      },

      backward(){
        step.value -= 1
      },

      onSubmitCreateProject(){
        createProjectLoading.value = true
        projectApi.create({
          name: projectName.value
        }).then(resp => {
          projectId.value  = resp.data

          pipelineApi.createHello({
            projectId: projectId.value,
            name: 'Hello World',
            flagOneRun: true,
          }).then(resp => {
            qUtil.notifySuccess('创建项目完成')
            pipelineId.value = resp.data
            
            step.value += 1
            emit('stepTo', step.value)
          }, resp => {
            qUtil.notifyError(resp.message)
          }).finally(()=>{
            createProjectLoading.value = false
          })
          
        }, resp => {
          createProjectLoading.value = false
          qUtil.notifyError(resp.message)
        })
      },

      onSubmitCreateAgentJoinScript(){

        const startAgentWaiting = () => {
           flagAgentWaiting.value = true

           if(agentWaitingTimer){
             clearInterval(agentWaitingTimer)
           }

           agentWaitingTimer = setInterval(() => {
             agentApi.list({ agentId: agentId.value}).then(resp => {
               const page = resp.data.page
               if(page){
                 page.forEach(agent => {
                   if(agent.agentId == agentId.value){
                     clearInterval(agentWaitingTimer)
                     flagAgentWaiting.value = false
                     flagAgentJoined.value = true
                   }
                 })
               }
             })
           }, 5000)
        }

        const generateScript = () => {
          agentApi.generateJoinScript({
            osType: agentOsType.value,
            agentId: agentId.value,
            accessToken: agentAccessToken.value,
            serverHost: agentServerHost.value,
            serverRpcPort: agentServerRpcPort.value,
            serverRestPort: agentServerRestPort.value,
            serverRestUseSsl: agentServerRestUseSsl.value,
            dataDir: agentDataDir.value,
          }).then(resp => {
            agentJoinScript.value = resp.data

            startAgentWaiting()

            try{
              const fileName = `y20-agent-client-install-${agentId.value}`
              switch(agentOsType.value){
                case 'WINDOWS':
                  download(`${fileName}.ps1`, agentJoinScript.value, true)
                  return
                case 'LINUX':
                  download(`${fileName}.sh`, agentJoinScript.value)
                  return
              }
            }catch(err){
              qUtil.notifyError('下载接入脚本发生错误')
            }

            // copyToClipboard(agentJoinScript.value).then(() => {
            //   qUtil.notifySuccess('脚本已复制到粘贴板')
            // }).catch(() => {
            //   qUtil.notifyError('复制脚本到粘贴板发生错误')
            // })
          }, resp => {
            qUtil.notifyError('生成接入脚本发生错误')
          }).finally(() => {
            createAgentJoinScriptLoading.value = false
          })
        }

        const generateAccessTokenAndScript = () => {
          const now = dayjs()
          const beginTime = now.format('YYYY-MM-DD HH:mm:ss')
          const endTime = now.add(1, 'year').format('YYYY-MM-DD HH:mm:ss')

          createAgentJoinScriptLoading.value = true
          authApi.createAgentAccessToken({
            name: `${agentId.value}-自动生成`,
            beginTime,
            endTime,
          }).then(resp => {
            const token = resp.data
            agentAccessToken.value = token

            generateScript()
          }, resp => {
            createAgentJoinScriptLoading.value = false
            qUtil.notifyError(resp.message || '自动生成令牌发生错误')
          })
        }

        if(agentAccessToken.value){
          generateScript()
        }else{
          generateAccessTokenAndScript()
        }

      },

      onSubmitCreateRun(){
        createRunLoading.value = true
        pipelineApi.createRun({
          projectId: projectId.value,
          pipelineId: pipelineId.value,
          name: dayjs().format('新手指引 YYYYMMDDHHmmss'),
          inParams: {
            AGENT: agentId.value
          },
          flagDebug: false,
        }).then(resp => {
          const pipelineRunId = resp.data
          router.push({ 
            path: `/project/${projectId.value}/pipeline/${pipelineId.value}/run/${pipelineRunId}`,
            query: {
              start: true,
              tail: true,
            }  
          })
        }, resp => {
          qUtil.notifyError(resp.message)
        }).finally(()=>{
          createRunLoading.value = false
        })
      },
    }
  },
}
</script>