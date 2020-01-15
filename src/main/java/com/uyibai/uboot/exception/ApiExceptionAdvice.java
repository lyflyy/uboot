package com.uyibai.uboot.exception;

import com.uyibai.uboot.common.vo.ArgumentInvalidResult;
import com.uyibai.uboot.common.vo.ResultDataMsg;
import com.uyibai.uboot.common.vo.ResultDataWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 如果异常需要打印 堆栈信息 抛出: BizException
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2018/3/17
 */
@RestControllerAdvice
public class ApiExceptionAdvice {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    /**
     * 业务异常统一包装BizException
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResultDataWrap handleBizException(HttpServletRequest request, BizException e) {
        LOGGER.warn("BizException-> errorMsg:{}, httpStatus:{}, uri:{}", e.getErrorMsg(), getStatus(request).value(), request.getRequestURI(), e);
        return new ResultDataWrap(e.getMessage(), e.getCode());
    }

    /**
     * 统一的 参数验证错误返回
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public ResultDataWrap methodArgumentNotValidHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        LOGGER.info("MethodArgumentNotValidException-> uri:{},bindingResult:{}", request.getRequestURI(), e.getBindingResult());
        //按需重新封装需要返回的错误信息
        try {
            return new ResultDataWrap(e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), ResultDataMsg.PARAMETER_ERROR.getStatus());
        } catch (Exception e1) {
            return new ResultDataWrap("参数验证错误！", ResultDataMsg.PARAMETER_ERROR.getStatus());
        }
    }

    /**
     * 缺少参数 异常格式化
     *
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResultDataWrap missingServletRequestParameterHandler(HttpServletRequest request,
                                                                MissingServletRequestParameterException exception) {
        //按需重新封装需要返回的错误信息
        LOGGER.warn("MethodArgumentNotValidException-> uri:{}", request.getRequestURI(), exception);
        ArgumentInvalidResult invalidArgument = new ArgumentInvalidResult();
        String msg = "参数'" + exception.getParameterName() + "[" + exception.getParameterType() + "]' 不能为空";
        invalidArgument.setDefaultMessage(msg);
        invalidArgument.setField(exception.getParameterName());
        invalidArgument.setType(exception.getParameterType());
        ResultDataMsg errorMsg = ResultDataMsg.PARAMETER_ERROR;
        errorMsg.setMsg(msg);
        return new ResultDataWrap(errorMsg);
    }


    // 捕捉其他所有异常
    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultDataWrap globalException(HttpServletRequest request, Throwable ex) {
        LOGGER.error("Exception-> httpStatus:{}, uri:{}, errorMsg:{}", getStatus(request).value(), request.getRequestURI(), ex.getMessage(), ex);
        return new ResultDataWrap(ResultDataMsg.DEFAULT_ERROR);
    }

    //    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultDataWrap messageNotReadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
        LOGGER.error("请求参数不匹配。", exception);
        return new ResultDataWrap("请求参数不匹配", getStatus(request).value());
    }


    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
