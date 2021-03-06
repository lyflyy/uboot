package org.uboot.modules.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecgframework.poi.excel.annotation.Excel;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.uboot.common.aspect.annotation.Dict;
import org.uboot.common.system.base.entity.BaseEntity;

import java.util.List;

/**
 * @Description: 消息模板
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_sms_template")
public class SysMessageTemplate extends BaseEntity {
	/**模板CODE*/
	@Excel(name = "模板CODE", width = 15)
	private String templateCode;
	/**模板标题*/
	@Excel(name = "模板标题", width = 30)
	private String templateName;
	/**模板内容*/
	@Excel(name = "模板内容", width = 50)
	private String templateContent;
	/**模板测试json*/
	@Excel(name = "模板测试json", width = 15)
	private String templateTestJson;
	/**模板类型*/
	@Excel(name = "模板类型", width = 15)
	private String templateType;

    @Excel(name="删除状态",width=15)
    @Dict(dicCode = "del_flag")
    @TableLogic
    private String delFlag;

    /**优先级（L低，M中，H高）*/
    @Dict(dicCode = "message_priority")
    private java.lang.String priority;

    @TableField(exist = false)
    private List<String> titleParam;

    @TableField(exist = false)
    private List<String> contentParam;
}
