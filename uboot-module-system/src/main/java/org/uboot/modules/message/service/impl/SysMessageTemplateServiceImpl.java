package org.uboot.modules.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uboot.modules.message.entity.SysMessageTemplate;
import org.uboot.modules.message.mapper.SysMessageTemplateMapper;
import org.uboot.modules.message.service.ISysMessageTemplateService;
import org.uboot.modules.system.entity.SysDepart;
import org.uboot.modules.system.mapper.SysDepartMapper;
import org.uboot.modules.system.service.ISysDepartService;

import java.util.List;

/**
 * @Description: 消息模板
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageTemplateServiceImpl extends ServiceImpl<SysMessageTemplateMapper, SysMessageTemplate> implements ISysMessageTemplateService {

    @Autowired
    private SysMessageTemplateMapper sysMessageTemplateMapper;


    @Override
    public List<SysMessageTemplate> selectByCode(String code) {
        return sysMessageTemplateMapper.selectByCode(code);
    }
}
