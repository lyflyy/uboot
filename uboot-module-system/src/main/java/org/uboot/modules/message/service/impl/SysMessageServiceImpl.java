package org.uboot.modules.message.service.impl;

import org.uboot.common.system.base.service.impl.JeecgServiceImpl;
import org.uboot.modules.message.entity.SysMessage;
import org.uboot.modules.message.mapper.SysMessageMapper;
import org.uboot.modules.message.service.ISysMessageService;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageServiceImpl extends JeecgServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

}
