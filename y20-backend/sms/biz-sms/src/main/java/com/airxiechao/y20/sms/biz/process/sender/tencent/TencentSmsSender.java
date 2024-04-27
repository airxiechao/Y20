package com.airxiechao.y20.sms.biz.process.sender.tencent;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.sms.biz.process.sender.ISmsSender;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;

public class TencentSmsSender implements ISmsSender {

    private static final TencentSmsConfig config = ConfigFactory.get(TencentSmsConfig.class);

    @Override
    public void sendVerificationCode(String mobile, String code) throws Exception {
        String[] templateParamSet = {code};
        send(mobile, config.getVerificationCodeTemplateId(), templateParamSet);
    }

    @Override
    public void sendMonitorAlert(String mobile, String monitorName) throws Exception {
        String[] templateParamSet = {monitorName};
        send(mobile, config.getMonitorAlertTemplateId(), templateParamSet);
    }

    private void send(String mobile, String templateId, String[] templateParamSet) throws Exception {
        Credential cred = new Credential(config.getSecretId(), config.getSecretKey());
        // 实例化一个http选项，可选，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        // 设置代理
        // httpProfile.setProxyHost("真实代理ip");
        // httpProfile.setProxyPort(真实代理端口);
        /* SDK默认使用POST方法。
         * 如果你一定要使用GET方法，可以在这里设置。GET方法无法处理一些较大的请求 */
        httpProfile.setReqMethod("POST");
        /* SDK有默认的超时时间，非必要请不要进行调整
         * 如有需要请在代码中查阅以获取最新的默认值 */
        httpProfile.setConnTimeout(60);
        /* SDK会自动指定域名。通常是不需要特地指定域名的，但是如果你访问的是金融区的服务
         * 则必须手动指定域名，例如sms的上海金融区域名： sms.ap-shanghai-fsi.tencentcloudapi.com */
        httpProfile.setEndpoint("sms.tencentcloudapi.com");
        /* 非必要步骤:
         * 实例化一个客户端配置对象，可以指定超时时间等配置 */
        ClientProfile clientProfile = new ClientProfile();
        /* SDK默认用TC3-HMAC-SHA256进行签名
         * 非必要请不要修改这个字段 */
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);
        /* 实例化要请求产品(以sms为例)的client对象
         * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，或者引用预设的常量 */
        SmsClient client = new SmsClient(cred, "ap-guangzhou",clientProfile);
        /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
         * 你可以直接查询SDK源码确定接口有哪些属性可以设置
         * 属性可能是基本类型，也可能引用了另一个数据结构
         * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
        SendSmsRequest req = new SendSmsRequest();
        /* 填充请求参数,这里request对象的成员变量即对应接口的入参
         * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
         * 基本类型的设置:
         * 帮助链接：
         * 短信控制台: https://console.cloud.tencent.com/smsv2
         * sms helper: https://cloud.tencent.com/document/product/382/3773 */
        /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
        String sdkAppId = config.getSdkAppId();
        req.setSmsSdkAppId(sdkAppId);
        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 */
        String signName = config.getSignName();
        req.setSignName(signName);
        /* 国际/港澳台短信 SenderId: 国内短信填空，默认未开通，如需开通请联系 [sms helper] */
        String senderid = "";
        req.setSenderId(senderid);
        /* 用户的 session 内容: 可以携带用户侧 ID 等上下文信息，server 会原样返回 */
        String sessionContext = "";
        req.setSessionContext(sessionContext);
        /* 短信号码扩展号: 默认未开通，如需开通请联系 [sms helper] */
        String extendCode = "";
        req.setExtendCode(extendCode);
        /* 模板 ID: 必须填写已审核通过的模板 ID。模板ID可登录 [短信控制台] 查看 */
        req.setTemplateId(templateId);
        /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
         * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
        if(!mobile.startsWith("+")){
            mobile = "+86" + mobile;
        }
        String[] phoneNumberSet = {mobile};
        req.setPhoneNumberSet(phoneNumberSet);
        /* 模板参数: 若无模板参数，则设置为空 */
        req.setTemplateParamSet(templateParamSet);
        /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
         * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
        SendSmsResponse res = client.SendSms(req);
        if(res.getSendStatusSet().length > 0){
            String status = res.getSendStatusSet()[0].getCode();
            String message = res.getSendStatusSet()[0].getMessage();
            if(status.equals("Ok")){
                return;
            }

            throw new Exception(message);

        }

        throw new Exception("send sms error");
    }
}
