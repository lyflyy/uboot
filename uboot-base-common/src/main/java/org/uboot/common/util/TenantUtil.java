package org.uboot.common.util;

import static org.uboot.common.constant.TenantConstant.X_ACCESS_TENANT;
import static org.uboot.common.util.SpringContextUtils.getHttpServletRequest;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-03 19:23
 * @Description:
 **/
public class TenantUtil {

    public static String getTenantId(){
        return getHttpServletRequest().getHeader(X_ACCESS_TENANT);
    }

}
