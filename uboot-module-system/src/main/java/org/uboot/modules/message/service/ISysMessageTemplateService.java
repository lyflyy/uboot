package org.uboot.modules.message.service;

import java.util.List;

import org.uboot.common.system.base.service.IBaseService;
import org.uboot.modules.message.entity.SysMessageTemplate;

/**
 * @Description: 消息模板
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
public interface ISysMessageTemplateService extends IBaseService<SysMessageTemplate> {
    List<SysMessageTemplate> selectByCode(String code);
}
