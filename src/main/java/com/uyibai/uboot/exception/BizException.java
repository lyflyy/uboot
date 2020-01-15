/*
 * Copyright 2015-2102 RonCoo(http://www.roncoo.com) Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uyibai.uboot.exception;

import com.uyibai.uboot.common.vo.ResultDataMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 业务异常基类，所有业务异常都必须继承于此异常 .
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2016/11/17  下午9:07
 */
public class BizException extends Exception {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = -5875371379845226068L;

    // 用户相关错误编码 10030001

    public static final BizException DEFAULT_EXCEPTION = new BizException(ResultDataMsg.DEFAULT_ERROR);

    /**
     * 异常信息
     */
    protected String msg;

    /**
     * 具体异常码
     */
    protected int code;

    public BizException(int code, String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
        this.code = code;
        this.msg = String.format(msgFormat, args);
    }

    public BizException(ResultDataMsg resultDataMsg, Object... args) {
        super(String.format(resultDataMsg.getMsg(), args));
        this.code = resultDataMsg.getStatus();
        this.msg = String.format(resultDataMsg.getMsg(), args);
    }

    public BizException(ResultDataMsg resultDataMsg) {
        super(resultDataMsg.getMsg());
        this.code = resultDataMsg.getStatus();
        this.msg = resultDataMsg.getMsg();
    }

    public BizException() {
        super();
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message) {
        super(message);
        this.code = 500;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public void print() {
        logger.info("==>{},code:{},msg:{}", this.getClass().getName(), this.code, this.msg);
    }

    public ResultDataMsg getErrorMsg() {
        return new ResultDataMsg(this.code, this.msg);
    }

}
