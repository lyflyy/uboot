package org.uboot.modules.message.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.uboot.modules.message.entity.SysMessageTemplate;
import org.uboot.modules.system.entity.SysDepart;

import java.util.List;

/**
 * @Description: 消息模板
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
public interface ISysMessageTemplateService extends IService<SysMessageTemplate>{

    SysMessageTemplate selectByCode(String code);
}
