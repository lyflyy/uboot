package com.uyibai.uboot.common.vo;

import lombok.Data;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2018/3/31
 */
@Data
public class ArgumentInvalidResult {
    private String field;
    private Object rejectedValue;
    private String defaultMessage;
    private String type;
}
