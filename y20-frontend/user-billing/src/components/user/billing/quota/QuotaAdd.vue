<template>
  <LayoutTwoColumn class="quota-add">
    <template v-slot:center>
      <q-dialog persistent v-model="flagShowPayDialog">
        <q-card flat class="full-width">
          <q-card-section>
            <div v-if="payType == 'WXPAY'" class="text-h6 text-center">微信支付</div>
            <div v-else-if="payType == 'ALIPAY'" class="text-h6 text-center">支付宝</div>
          </q-card-section>
          <q-card-section>
            <div v-if="payType == 'WXPAY'">
              <div class="qr" ref="payQr"></div>
              <div class="q-mt-sm text-center text-grey-7">
                <span>请使用微信扫码付款</span>
              </div>
            </div>
            <div v-else-if="payType == 'ALIPAY'">
              <form ref="payForm" target="_blank" method="post" :action="payFormAction">
                <input hidden name="biz_content" :value="payFormBizContent" />
              </form>
              <div class="q-pa-lg text-center">
                <q-spinner
                  color="blue"
                  size="80px"
                />
              </div>
              <div class="q-mt-sm text-center text-grey-7">
                <span>请在弹出页面使用支付宝付款</span>
              </div>
            </div>
          </q-card-section>
          <q-card-actions align="center">
            <q-btn flat color="primary" label="关闭" v-close-popup />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <q-dialog persistent v-model="flagShowPayedDialog">
        <q-card flat class="full-width">
          <q-card-section>
            <div class="text-center text-green">
              <div>
                <q-icon name="check" color="green" size="80px" />
              </div>
              <div class="text-h6">支付完成</div>
            </div>
          </q-card-section>
          <q-card-actions align="center">
            <q-btn flat label="完成" color="primary" v-close-popup to="/user/billing/quota" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="配额" to="/user/billing/quota" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-md bg-white">
            <div class="q-pb-md">购买配额</div>
            <div>
              <div class="row q-col-gutter-md">
                <template v-if="priceLoading">
                  <div class="col-12 col-sm-6" v-for="(_, i) in [1,2]" :key="i">
                    <q-card flat class="q-pa-md">
                      <q-skeleton type="text" width="25%" />
                      <q-skeleton type="text" class="q-mt-sm" width="50%" />
                      <q-skeleton type="text" width="12%" />
                      <q-separator class="q-my-md" />
                      <q-skeleton type="text" />
                      <q-skeleton type="text" />
                    </q-card>
                  </div>
                </template>
                <template v-else>
                  <div class="col-12 col-sm-6" v-for="(sale, i) in sales" :key="i">
                    <q-card
                      :flat="billingPlan != sale.name"
                      :bordered="billingPlan != sale.name" 
                      class="q-pa-md cursor-pointer" @click="onClickBillingPlan(sale.name, sale.price, sale.discounts)">
                      <div>
                        <span class="vertical-middle">{{sale.name}}</span>
                        <q-badge v-if="billingPlan == sale.name" class="vertical-middle q-ml-sm" label="当前选择"/>
                      </div>
                      <div class="q-mt-md">￥ <span class="text-h5">{{(sale.price/100).toFixed(1)}}</span></div>
                      <div class="text-grey">每月</div>
                      <div>
                        <q-badge outline color="primary" class="q-mr-xs" v-for="discount in sale.discounts" :key="discount.id" :label="discountText(discount)" />
                      </div>
                      <q-separator class="q-my-md" />
                      <div v-for="(item, ii) in sale.items" :key="ii">
                        <q-icon class="vertical-middle q-mr-xs" name="check_circle" color="primary" />
                        <span class="vertical-middle">{{item}}</span>
                      </div>
                    </q-card>
                  </div>
                </template>
              </div>

              <q-form
                @submit="onSubmit"
                class="q-gutter-md q-mt-md"
              >
                <div class="text-grey-7">购买月数</div>
                <q-btn-toggle
                  class="q-mb-sm"
                  v-model="numMonth"
                  toggle-color="primary"
                  :options="[
                    {label: '1个月', value: 1},
                    {label: '3个月', value: 3},
                    {label: '6个月', value: 6},
                    {label: '12个月', value: 12},
                  ]"
                />

                <!-- <q-input
                  outlined
                  v-model="numMonth"
                  type="number"
                  :min="1"
                  :max="12"
                  label="购买月数"
                  hint="请输入购买月数（1~12）"
                  lazy-rules
                  :rules="[ val => !!val && val >= 1 && val <= 12 || '请输入购买月数（1~12）']"
                /> -->

                <q-select
                  outlined
                  label="支付方式"
                  :options="payTypes"
                  v-model="payType" 
                  emit-value 
                  map-options 
                />

                <q-item class="text-weight-bold bg-grey-1">
                  <q-item-section class="text-h6">总计：</q-item-section>
                  <q-item-section side>
                    <span class="text-red text-h6">￥ {{(total/100).toFixed(2)}} 元</span>
                    <span v-if="total != originalTotal" class="text-subtitle2" style="text-decoration: line-through;">原价 ￥ {{(originalTotal/100).toFixed(2)}} 元</span>
                  </q-item-section>
                </q-item>

                <q-separator />

                <div>
                  <q-btn unelevated label="支付" type="submit" color="primary" :loading="orderLoading" />
                </div>
              </q-form>
            </div>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss" scoped>
.quota-add{
  
}

.qr{
  margin: 0 auto;
  width: 202px;
  height: 202px;
  border: 1px solid #ddd;
}

</style>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, watch, onMounted, onUnmounted } from 'vue'
import QRCode from 'qrcodejs2'

import payApi from '@/api/pay.api'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const store = inject('useStore')()
    const router = inject('useRouter')()

    const sales = ref([])

    const billingPlan = ref(null)
    const billingPlanPrice = ref(0)
    const billingPlanDiscounts = ref([])
    const numMonth = ref(1)

    const total = computed(() => {
      let discountRate = 1
      for(let i = 0; i < billingPlanDiscounts.value.length; ++i){
        const discount = billingPlanDiscounts.value[i]
        if(numMonth.value >= discount.numMin && numMonth.value < discount.numMax){
          discountRate = discount.rate
          break
        }
      }
      return billingPlanPrice.value * numMonth.value * discountRate
    })

    const originalTotal = computed(() => {
      return billingPlanPrice.value * numMonth.value
    })

    const payTypes = ref([
      {label: '微信', value: 'WXPAY'},
      {label: '支付宝', value: 'ALIPAY'},
    ])
    const payType = ref('WXPAY')

    const priceLoading = ref(false)
    const orderLoading = ref(false)
    const flagShowPayDialog = ref(false)
    const flagShowPayedDialog = ref(false)
    const payQr = ref(null)
    const payQrUrl = ref(null)
    const payForm = ref(null)
    const payFormAction = ref(null)
    const payFormBizContent = ref(null)
    const payOutTradeNo = ref(null)
    let payTimer = null

    const startPayTimer = () => {
      if(null == payTimer){
        payTimer = setInterval(() => {
          payApi.getQuotaOrder({
            outTradeNo: payOutTradeNo.value,
          }).then(resp => {
            const transaction = resp.data
            if(transaction.payed){
              flagShowPayDialog.value = false
              flagShowPayedDialog.value = true
            }
          })
        }, 2500)
      }
    }

    const stopPayTimer = () => {
      if(payTimer){
        clearInterval(payTimer)
        payTimer = null
      }
    }

    watch(flagShowPayDialog, (newValue) => {
      if(!newValue){
        stopPayTimer()
      }
    })

    onMounted(() => {
      priceLoading.value = true
      payApi.getQuotaPrice().then(resp => {
        const { basic, pro, basicDiscounts, proDiscounts } = resp.data

        billingPlan.value = basic.billingPlan
        billingPlanPrice.value = basic.price
        billingPlanDiscounts.value = basicDiscounts || []

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
        priceLoading.value = false
      }, resp => {
        qUtil.notifyError(resp.message || '查询配额价格发生错误')
      })
    })

    onUnmounted(() => {
      stopPayTimer()
    })

    return {
      sales,
      
      billingPlan,
      billingPlanPrice,
      billingPlanDiscounts,
      numMonth,

      total,
      originalTotal,

      payTypes,
      payType,

      priceLoading,
      orderLoading,
      flagShowPayedDialog,
      flagShowPayDialog,
      payQr,
      payQrUrl,
      payForm,
      payFormAction,
      payFormBizContent,
      payOutTradeNo,

      discountText(discount){
        return `${discount.numMin}个月${(discount.rate*10).toFixed(1)}折`
      },

      onClickBillingPlan(name, price, discounts){
        billingPlan.value = name
        billingPlanPrice.value = price
        billingPlanDiscounts.value = discounts
      },

      onSubmit(){
        
        if(total.value <= 0){
          qUtil.notifyError('购买金额必须大于0')
          return
        }

        orderLoading.value = true
        payApi.orderQuotaQr({
          billingPlan: billingPlan.value,
          numMonth: numMonth.value,
          payType: payType.value
        }).then(resp => {
          flagShowPayDialog.value = true
          const { outTradeNo, qrUrl, action, bizContent } = resp.data
          
          switch(payType.value){
            case 'WXPAY':
              payQrUrl.value = qrUrl
              payOutTradeNo.value = outTradeNo
              setTimeout(() => {
                new QRCode(payQr.value, {
                  text: payQrUrl.value,
                  width: 200,
                  height: 200,
                  colorDark : "#000000",
                  colorLight : "#ffffff",
                  correctLevel : QRCode.CorrectLevel.H
                }) 
              }, 1)
              break;
            case 'ALIPAY':
              payFormAction.value = action
              payFormBizContent.value = bizContent
              payOutTradeNo.value = outTradeNo
              setTimeout(() => {
                payForm.value.submit()
              }, 100)
              break;
          }

          startPayTimer()
        }, resp => {
          qUtil.notifyError(resp.message || '支付下单发生错误')
        }).finally(() => {
          orderLoading.value = false
        })
      },
    }
  },
}
</script>