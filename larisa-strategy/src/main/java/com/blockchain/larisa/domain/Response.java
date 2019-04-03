package com.blockchain.larisa.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class Response {

    /**
     * 数据所属的channel, 格式: market.$symbol.detail.merged
     */
    private String ch;

    /**
     * 请求处理结果
     */
    private String status;

    /**
     * 错误码
     */
    private Integer errCode;

    /**
     * 错误消息
     */
    private String errMsg;

    /**
     * 响应生成的时间点, 单位: 毫秒
     */
    private Long ts;

    public boolean isSuccess() {
        return "ok".equals(status);
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getErrCode() {
        return errCode;
    }

    @JSONField(name = "err_code")
    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    @JSONField(name = "err_msg")
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

}
