package com.uyibai.uboot.common.vo;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 通用的 返回结果对象，主要用于api接口，其他地方也可以使用
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2016/11/17  下午9:07
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultDataWrap implements Serializable {

    private static final long serialVersionUID = 1L;

    // 提示信息
    private String msg = "success";

    // 通过 ErrorMsg类以及它的子类对没一个错误进行编码
    private Integer status = 200;
    // 返回时携带的数据
    private Object data;

    public ResultDataWrap() {
    }

    public ResultDataWrap(String msg, Integer status, Object data) {
        this.msg = msg;
        this.status = status;
        this.data = data;
    }


    public ResultDataWrap(String msg) {
        this.msg = msg;
    }

    public ResultDataWrap(Object data) {
        this.data = data;
    }


    public ResultDataWrap(ResultDataMsg resultDataMsg) {
        this.data = new HashMap<>();
        this.status = resultDataMsg.getStatus();
        this.msg = resultDataMsg.getMsg();
    }

    public ResultDataWrap(ResultDataMsg resultDataMsg, Object data) {
        this.data = data;
        this.status = resultDataMsg.getStatus();
        this.msg = resultDataMsg.getMsg();
    }

    public ResultDataWrap(String msg, Integer status) {
        this.msg = msg;
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public ResultDataWrap setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public ResultDataWrap setStatus(Integer status) {
        this.status = status;
        return this;
    }

    /**
     * 如果 业务数据为null 也要返回 data字段给客户端
     *
     * @return
     */
    public Object getData() {
        return null == data ? new HashMap<>(): data;
    }

    public ResultDataWrap setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "ResultDataWrap{" +
                "msg='" + msg + '\'' +
                ", status=" + status +
                ", data=" + data +
                '}';
    }
}
