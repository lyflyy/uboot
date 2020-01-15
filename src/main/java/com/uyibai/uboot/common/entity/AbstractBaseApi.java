package com.uyibai.uboot.common.entity;

import com.uyibai.uboot.common.vo.ResultDataMsg;
import com.uyibai.uboot.common.vo.ResultDataWrap;
import com.uyibai.uboot.utils.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractBaseApi {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private String viewPrefix;

    public AbstractBaseApi() {
    }

    public AbstractBaseApi(String viewPrefix) {
        this.viewPrefix = viewPrefix;
    }

    public String redirect(String viewName) {
        return "redirect:" + viewName;
    }

    public String render(String viewName) {
        return this.viewPrefix + viewName;
    }

    public ResultDataWrap buildResult() {
        return new ResultDataWrap(ResultDataMsg.DEFAULT_SUCCESS);
    }

    public ResultDataWrap buildFailResult() {
        return new ResultDataWrap(ResultDataMsg.DEFAULT_ERROR);
    }

    public ResultDataWrap buildResult(ResultDataMsg resultMsg) {
        return new ResultDataWrap(resultMsg);
    }

    public ResultDataWrap buildFailResult(ObjectError error) {
        ResultDataMsg resultMsg = new ResultDataMsg();
        resultMsg.setMsg(error.getDefaultMessage());
        resultMsg.setStatus(ResultDataMsg.PARAMETER_ERROR.getStatus());
        return new ResultDataWrap(resultMsg);
    }

    public String getUserAgent() {
        return RequestUtil.getUserAgent();
    }

    public String getClientIP() {
        return RequestUtil.getClientIP();
    }

    public boolean isIpString(String ipStr) {
        return RequestUtil.isIpString(ipStr);
    }

    public HttpServletRequest getRequest() {
        return RequestUtil.getRequest();
    }
}
