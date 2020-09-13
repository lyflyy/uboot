package org.uboot.modules.system.util;

/**
 * 租户上下文
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2020/9/13
 */
public class TenantContext {
  private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

  public static void set(String tenantId) {
    TENANT_ID.set(tenantId);
  }

  public static String get() {

    return TENANT_ID.get();
  }

  public static void remove() {
    TENANT_ID.remove();
  }
}
