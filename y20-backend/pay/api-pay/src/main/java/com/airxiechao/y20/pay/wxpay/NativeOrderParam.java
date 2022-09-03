package com.airxiechao.y20.pay.wxpay;

public class NativeOrderParam {
    private String appid;
    private String mchid;
    private String description;
    private String out_trade_no;
    private String attach;
    private String notify_url;
    private Amount amount;

    public NativeOrderParam(String appid, String mchid, String description, String out_trade_no, String attach, String notify_url, int total) {
        this.appid = appid;
        this.mchid = mchid;
        this.description = description;
        this.out_trade_no = out_trade_no;
        this.attach = attach;
        this.notify_url = notify_url;
        this.amount = new Amount(total);
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public static class Amount{
        private int total;

        public Amount(int total) {
            this.total = total;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
