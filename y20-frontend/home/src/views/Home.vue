<template>
  <div class="page-home">
    <div class="support z-top fixed-bottom-right" :style="{
      'right': '30px',
      'bottom': '30px',
    }">
      <q-btn round :size="$q.screen.gt.sm ? 'lg' : 'md'" class="text-grey-7" icon="support_agent">
        <q-popup-proxy anchor="center start" self="center right" :offset="[20, 0]">
          <q-card class="q-pa-md">
            <q-img
              alt="qr-xiechao"
              :src="`/${FRONTEND_SERVICE_NAME}/static/img/qr-xiechao.png`"
              style="height: 150px; width: 150px"
            />
            <div class="q-mt-sm text-center text-grey-7">
              请微信扫码添加客服
            </div>
          </q-card>
        </q-popup-proxy>
      </q-btn>
    </div>
    <q-toolbar class="page-home-toolbar">
      <q-toolbar-title class="text-bold">
        <q-icon class="q-mr-sm vertical-middle" size="4rem" :name="`img:/icon-logo.png`" />
        <span class="vertical-middle">鲲擎运维 Y20</span>
      </q-toolbar-title>
      <div class="gt-xs">
        <a href="https://github.com/airxiechao/Y20" class="q-mx-md" target="_blank">
          <img class="vertical-middle" alt="icon-github" :src="`/${FRONTEND_SERVICE_NAME}/static/img/icon-github.png`"/>
        </a>
        <q-btn style="padding: 0 20px;" class="q-mx-md text-dark" rounded size="lg" type="a" href="/nav/workspace/template" flat label="应用" />
        <q-btn style="padding: 0 20px;" class="q-mx-md text-dark" rounded size="lg" type="a" href="/docs" flat label="文档" />
        <q-btn style="padding: 0 20px;" class="q-mx-md text-dark" rounded size="lg" type="a" target="_blank" href="https://www.zhihu.com/column/c_1500492490684149761" flat label="博客" />
        <q-btn style="padding: 0 20px;" class="q-mx-md text-dark" rounded type="a" href="/nav/login" size="lg" flat label="登录" />
        <q-btn style="padding: 0 20px;" class="q-ml-md text-dark" rounded size="lg" type="a" href="/nav/signup" unelevated label="注册" />
      </div>
      <div class="lt-sm">
        <q-btn class="text-dark" size="lg" flat dense round icon="more_vert">
          <q-menu anchor="bottom right" self="top right">
            <q-list>
              <q-item class="text-dark" clickable v-close-popup tag="a" href="/nav/workspace/template">
                <q-item-section>应用</q-item-section>
              </q-item>
              <q-item class="text-dark" clickable v-close-popup tag="a" href="/docs">
                <q-item-section>文档</q-item-section>
              </q-item>
              <q-item class="text-dark" clickable v-close-popup tag="a" target="_blank" href="https://www.zhihu.com/column/c_1500492490684149761">
                <q-item-section>博客</q-item-section>
              </q-item>
              <q-separator />
              <q-item class="text-dark" clickable v-close-popup tag="a" href="/nav/login">
                <q-item-section>登录</q-item-section>
              </q-item>
              <q-item class="text-dark" clickable v-close-popup tag="a" href="/nav/signup">
                <q-item-section>注册</q-item-section>
              </q-item>
            </q-list>
          </q-menu>
        </q-btn>
      </div>
    </q-toolbar>
    
    <div class="page-home-content absolute-top">

      <div class="sec top" :style="{
        'padding-top': $q.screen.gt.sm ? '140px' : '120px',
        'padding-bottom': $q.screen.gt.sm ? '50px' : '25px',
      }">
        <div class="row middle">
          <div class="col-12">
            <div :class="{
              'text-h3': $q.screen.gt.sm,
              'text-h4': $q.screen.lt.md, 
              'text-center': true,
              'text-dark': true,
              'text-weight-bold': true,
            }">分布式系统的自动化运维</div>
            <div class="q-py-md text-grey-7 text-center" style="font-size: 16px; line-height: 36px;"></div>
            <div class="q-mt-sm q-mb-xl text-center">
              <q-btn flat class="q-px-lg q-ma-sm bg-primary text-white" size="lg" label="开始使用" @click="onClickStart"/>
              <q-btn flat class="q-px-lg q-ma-sm bg-white text-dark" size="lg" label="入门指南" type="a" href="/docs" />
            </div>
            <!-- <div class="text-center">
              <img style="max-width: 100%;" alt="fig-home" :src="`/${FRONTEND_SERVICE_NAME}/static/img/fig-screen-home.png`"/>
            </div> -->
          </div>
        </div>
      </div>

      <div class="sec tab q-mt-md">
        <div class="middle">
          <div class="column box">
            <div class="col row">
              <div class="col q-pa-md self-center text-center text-h5"> 
                <div class="text-grey-7 text-subtitle2">我们的愿景</div>
                <div>满足开发者的运维需求</div>
              </div>
            </div>
            <div class="col row q-py-sm content-center">
              <div class="col-4 col-sm-2">
                <q-list>
                  <q-item>
                    <q-item-section class="content-center">
                      <q-avatar round style="background: #ecf2ff; color: #7c98ab;" icon="all_inclusive" size="60px" />
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section class="q-pa-sm text-center">
                      <q-item-label class="text-h6 q-mb-xs">流水线</q-item-label>
                      <q-item-label caption>可配置步骤、变量、文件、触发、队列、定时等等</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>
              <div class="col-4 col-sm-2">
                <q-list>
                  <q-item>
                    <q-item-section class="content-center">
                      <q-avatar round style="background: #ecf2ff; color: #7c98ab;" icon="computer" size="60px" />
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section class="q-pa-sm text-center">
                      <q-item-label class="text-h6 q-mb-xs">节点</q-item-label>
                      <q-item-label caption>支持Windows、Linux，自动生成接入脚本，连接加密</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>
              <div class="col-4 col-sm-2">
                <q-list>
                  <q-item>
                    <q-item-section class="content-center">
                      <q-avatar round style="background: #ecf2ff; color: #7c98ab;" icon="superscript" size="60px" />
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section class="q-pa-sm text-center">
                      <q-item-label class="text-h6 q-mb-xs">变量</q-item-label>
                      <q-item-label caption>提供文本、文件、节点、输入等类型变量</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>
              <div class="col-4 col-sm-2">
                <q-list>
                  <q-item>
                    <q-item-section class="content-center">
                      <q-avatar round style="background: #ecf2ff; color: #7c98ab;" icon="folder" size="60px" />
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section class="q-pa-sm text-center">
                      <q-item-label class="text-h6 q-mb-xs">文件</q-item-label>
                      <q-item-label caption>可存储到项目、流水线、步骤、运行实例</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>
              <div class="col-4 col-sm-2">
                <q-list>
                  <q-item>
                    <q-item-section class="content-center">
                      <q-avatar round style="background: #ecf2ff; color: #7c98ab;" icon="skip_next" size="60px" />
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section class="q-pa-sm text-center">
                      <q-item-label class="text-h6 q-mb-xs">调试</q-item-label>
                      <q-item-label caption>支持单步运行，可打开远程终端和文件浏览器</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>
              <div class="col-4 col-sm-2">
                <q-list>
                  <q-item>
                    <q-item-section class="content-center">
                      <q-avatar round style="background: #ecf2ff; color: #7c98ab;" icon="monitor" size="60px" />
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section class="q-pa-sm text-center">
                      <q-item-label class="text-h6 q-mb-xs">监视</q-item-label>
                      <q-item-label caption>支持对节点的进程和服务进行监视和动作</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="sec q-mt-md">
        <div class="row middle">
          <div class="sec-col col-12 col-sm-6">
            <div class="text">
              <div class="text-h5">
                <q-icon name="edit" class="vertical-middle q-mr-sm" />
                <span class="vertical-middle">灵活编排</span>
              </div>
              <div class="q-mt-md q-pl-md q-gutter-sm text-grey-7">
                <div>
                  <span class="vertical-middle">支持预定义变量和输入变量，方便存储凭证和配置启动参数。</span>
                </div>
                <div>
                  <span class="vertical-middle">支持文件，可以为项目和流水线预上传文件，或者启动时进行上传。</span>
                </div>
                <div>
                  <span class="vertical-middle">节点支持Windows和Linux系统，连接加密，自动生成接入脚本。</span>
                </div>
              </div>
            </div>
          </div>
          <div class="sec-col col-12 col-sm-6">
            <div class="text">
              <div class="text-h5">
                <q-icon name="restart_alt" class="vertical-middle q-mr-sm" />
                <span class="vertical-middle">实时观测</span>
              </div>
              <div class="q-mt-md q-pl-md q-gutter-sm text-grey-7">
                <div>
                  <span class="vertical-middle">流水线启动后，可以实时观测流水线执行状态变化和日志输出。</span>
                </div>
                <div>
                  <span class="vertical-middle">中途退出页面不会中断执行，再次进入可以继续观测。</span>
                </div>
                <div>
                  <span class="vertical-middle">流水线结束后，可查询执行结果和历史日志。</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="sec">
        <div class="row middle">
          <div class="sec-col col-12 col-sm-6">
            <div class="text">
              <div class="text-h5">
                <q-icon name="skip_next" class="vertical-middle q-mr-sm" />
                <span class="vertical-middle">轻松调试</span>
              </div>
              <div class="q-mt-md q-pl-md q-gutter-sm text-grey-7">
                <div>
                  <span class="vertical-middle">支持流水线的调试，流水线可以单步运行，和调试程序一样。</span>
                </div>
                <div>
                  <span class="vertical-middle">流水线运行时，可以打开当前节点的远程终端和文件浏览器。</span>
                </div>
                <div>
                  <span class="vertical-middle">节点接入后，支持通过远程终端，进行远程操作。</span>
                </div>
              </div>
            </div>
          </div>
          <div class="sec-col col-12 col-sm-6">
            <div class="text">
              <div class="text-h5">
                <q-icon name="more_vert" class="vertical-middle q-mr-sm" />
                <span class="vertical-middle">更多功能</span>
              </div>
              <div class="q-mt-md q-pl-md q-gutter-sm text-grey-7">
                <div>
                  <span class="vertical-middle">支持实时监测部署在节点的进程和服务的运行状态，发现错误时自动执行流水线。</span>
                </div>
                <div>
                  <span class="vertical-middle">流水线可以通过Git仓库远程触发。如果只允许运行一个实例，则进入等待队列。</span>
                </div>
                <div>
                  <span class="vertical-middle">支持加入团队，团队内的成员可以访问团队创建者工作空间的所有资源。</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="sec price q-my-xl">
        <div v-if="false" class="middle">
          <div class="row justify-center q-pb-md">
            <div class="text-h5 text-center">
              <div class="text-grey-7 text-subtitle2">配额 & 价格</div>
              <div>每月提供免费配额，更多配额可按需购买</div>
            </div>
          </div>
          <div class="row q-py-lg">
            <template v-if="saleLoading">
              <div class="col-12 col-sm-4 q-pa-sm" v-for="(_, i) in [1,2,3]" :key="i">
                <div class="price-card q-pa-md full-height">
                  <q-skeleton type="text" width="25%" />
                  <q-skeleton type="text" class="q-mt-sm" width="50%" />
                  <q-skeleton type="text" width="12%" />
                  <div class="q-mt-md" />
                  <q-skeleton type="text" />
                  <q-skeleton type="text" />
                </div>
              </div>
            </template>
            <template v-else>
              <div class="col-12 col-sm-4 q-pa-sm" v-for="(sale, i) in sales" :key="i">
                <div class="price-card q-pa-md full-height">
                  <div :class="{
                    'name': true,
                    'text-green': sale.name == '免费',
                  }">{{sale.name}}</div>
                  <div class="q-mt-md">￥ <span class="text-h5">{{(sale.price/100).toFixed(1)}}</span></div>
                  <div class="text-grey">每月</div>
                  <q-badge class="q-mr-xs" v-for="discount in sale.discounts" :key="discount.id" :label="discountText(discount)" />
                  <div class="q-mt-md" />
                  <div v-for="(item, ii) in sale.items" :key="ii">
                    <q-icon class="vertical-middle q-mr-xs" name="check" color="green" />
                    <span class="vertical-middle text-grey-7">{{item}}</span>
                  </div>
                </div>
              </div>
            </template>

          </div>
        </div>
      </div>

      <div class="footer q-pa-sm text-grey">
        <div class="info">
          <div class="row">
            <div class="col-12 col-md-3 q-pa-md">
              <div class="q-pb-sm">© {{ new Date().getFullYear() }} 成都鲲擎软件科技有限公司</div>
              <div><a class="q-mr-sm" style="color: inherit;" target="_blank" href="https://beian.miit.gov.cn">蜀ICP备20023213号-2</a></div>
              <div>
                <a class="q-mr-sm" style="color: inherit;" target="_blank" href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=51019002004441">
                  <img class="vertical-middle" alt="icon-gongan" :src="`/${FRONTEND_SERVICE_NAME}/static/img/icon-gongan.png`"/>
                  川公网安备 51019002004441号
                </a>
              </div>
            </div>
            <div class="col-12 col-md-3 q-pa-md">
              <div class="q-pb-sm">产品</div>
              <div>
                <a style="color: inherit;" target="_blank" href="https://y20.work">鲲擎运维 Y20</a>
              </div>
              <!-- <div>
                更新：{{LASTCOMMITDATETIME}}
              </div>
              <div>
                版本：{{VERSION}}
              </div> -->
            </div>
            <div class="col-12 col-md-3 q-pa-md">
              <div class="q-pb-sm">条款</div>
              <div>
                <a style="color: inherit;" target="_blank" href="/docs/terms-of-service.html">服务条款</a>
              </div>
            </div>
            <div class="col-12 col-md-3 q-pa-md">
              <div class="q-pb-sm">联系方式</div>
              <div>
                QQ群：923851253
              </div>
              <div>
                微信公众号：鲲擎软件
              </div>
              <div>
                邮箱：xiechao@y20.work
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/scss/reset';
@import '@/assets/scss/style';
@import '@/assets/scss/var';

$--max-width: 1280px !default;

.page-home{

  .support{
    background: #fff;
    border-radius: 50%;
  }

  &-toolbar{
    background-color: #f5f5f5;
    padding: 15px 20px;
    z-index: 999;
  }

  &-content{
    height: 100%;

    .sec{
      padding: 0 15px;

      .middle{
        max-width: $--max-width;
        margin: 0 auto;
      }

      .sec-col {
        word-break: break-all;
        padding: 15px;
      }

      .text{
        padding: 20px 40px;
        background: #fff;
        border-radius: 8px;
      }

      .fig{
        img {
          max-width: 100%;
          max-height: 480px;
        }
      }
    }

    .top{
      background: #f5f5f5;
    }
    
    .tab{
      margin-top: -60px;

      .middle{
        padding: 15px;
        
        .box{
          box-shadow: 0 2px 10px #ddd;
          background: #fff;
          border-radius: 8px;
        }
      }
    }
    
    .price{

      &-card{
        box-shadow: 0 2px 10px #ddd;
        border-radius: 8px;

        .name{
          
        }
      }
    }

    .footer{
      background-color: #f5f5f5;
      .info{
        line-height: 25px;
        max-width: $--max-width;
        margin: 0 auto;
      }
    }
  }
  
}
</style>


<script>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useQuasar } from 'quasar'

import payApi from '@/api/pay.api'
import quotaApi from '@/api/quota.api'

export default {
  components: {
  },
  setup (props, context) {
    const $q = useQuasar()
    const store = useStore()
    const sales = ref([])
    const saleLoading = ref(false)

    onMounted(() => {
      saleLoading.value = true
      quotaApi.getFreeQuota().then(resp => {
        const { freeQuotaNumAgent, freeQuotaNumPipelineRun } = resp.data
        sales.value.push({
          name: '免费',
          price: 0,
          items: [
            `${freeQuotaNumAgent} 个可接入节点`,
            `${freeQuotaNumPipelineRun} 次流水线运行`,
            '共享服务器资源',
          ],
        })

        payApi.getQuotaPrice().then(resp => {
          const { basic, pro, basicDiscounts, proDiscounts} = resp.data
          sales.value.push({
            name: basic.billingPlan,
            price: basic.price,
            discounts: basicDiscounts || [],
            items: [
              `${basic.numAgent} 个可接入节点`,
              `${basic.numPipelineRun} 次流水线运行`,
              '共享服务器资源',
            ],
          })
          sales.value.push({
            name: pro.billingPlan,
            price: pro.price,
            discounts: proDiscounts || [],
            items: [
              `${pro.numAgent} 个可接入节点`,
              `${pro.numPipelineRun} 次流水线运行`,
              '共享服务器资源',
            ],
          })

          saleLoading.value = false
        })
      })

      
    })

    return {
      VERSION: VERSION,
      LASTCOMMITDATETIME: LASTCOMMITDATETIME,
      FRONTEND_SERVICE_NAME: FRONTEND_SERVICE_NAME,

      $q,
      sales,
      saleLoading,

      discountText(discount){
        return `${discount.numMin}个月${(discount.rate*10).toFixed(1)}折`
      },

      onClickStart(){
        if(store.getters.accessToken){
          window.location.href  = '/nav/workspace/project'
        }else{
          window.location.href  = '/nav/login'
        }
      },
    }
  }
};
</script>

