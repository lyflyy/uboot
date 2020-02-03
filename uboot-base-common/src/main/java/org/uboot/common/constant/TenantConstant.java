package org.uboot.common.constant;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-03 19:46
 * @Description:
 **/
public interface TenantConstant {

    /**
     * requestHeader key -> 租户id
     */
    public static String X_ACCESS_TENANT = "X-Access-Tenant";

    /**
     * 系统租户字段
     */
    public static String TENTANT_SYS_ATTR = "tenantId";

    /**
     * 数据库租户存储属性名称
     */
    public static String TENTANT_DB_ATTR = "tenant_id";


}
