package com.uyibai.uboot.common.vo;

/**
 * 全局错误码
 * <p>
 * <pre>
 *     如果是其他模块可以单独定义错误码类继承此类，如：PayErrorMsg
 * </pre>
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2017/6/3  上午1:24
 */
public class ResultDataMsg {


    // 10030000  用户相关
    // 10036000  租户相关
    // 10031000  短信相关
    // 10040000  数据库相关,数据库事务相关,序列化相关
    // 10050000  内容相关
    // 10060000  内容相关

    public static final ResultDataMsg DEFAULT_SUCCESS = new ResultDataMsg(200, "success");

    public static final ResultDataMsg DEFAULT_ERROR = new ResultDataMsg(10010001, "接口请求异常");
    public static final ResultDataMsg PARAMETER_ERROR = new ResultDataMsg(10010002, "参数错误");
    public static final ResultDataMsg SERVER_ERROR = new ResultDataMsg(10010003, "服务器打盹了，请稍后重试");
    public static final ResultDataMsg ACTION_DEL_FAIL = new ResultDataMsg(10010004, "删除失败");
    public static final ResultDataMsg ACTION_UPDATE_FAIL = new ResultDataMsg(10010005, "修改失败");
    public static final ResultDataMsg ACTION_INSERT_FAIL = new ResultDataMsg(10010006, "添加失败");
    public static final ResultDataMsg ACTION_FAIL = new ResultDataMsg(10010007, "操作失败");
    // 内容错误码
    public static final ResultDataMsg CONTENT_POST_CHIEF_FAIL = new ResultDataMsg(10020001, "发布失败");
    public static final ResultDataMsg CONTENT_POST_FAIL = new ResultDataMsg(10020002, "发布失败");
    public static final ResultDataMsg CONTENT_POST_PUT_FAIL = new ResultDataMsg(10020003, "内容更新失败");
    public static final ResultDataMsg CONTENT_POST_DELETE_FAIL = new ResultDataMsg(10020004, "删除失败");

    public static final ResultDataMsg FILE_SUPORT_FAIL = new ResultDataMsg(10030001, "不支持此类文件上传");

    public static final ResultDataMsg FILE_UPLOAD_PROCESS_FAIL = new ResultDataMsg(10030002, "文件上传处理异常");

    public static final ResultDataMsg PAPER_SAVE_FAIL = new ResultDataMsg(10040001, "试卷保存失败");

    public static final ResultDataMsg EXAM_PLAN_SAVE_FAIL = new ResultDataMsg(10050001, "试卷保存失败");




    private Integer status;

    private String msg;

    public ResultDataMsg(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ResultDataMsg() {
    }

    public Integer getStatus() {
        return status;
    }

    public ResultDataMsg setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ResultDataMsg setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
