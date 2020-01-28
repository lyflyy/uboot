package org.uboot.modules.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.uboot.common.aspect.annotation.Dict;

/**
 * @Description: 方案表
 * @Author: jeecg-boot
 * @Date:   2020-01-27
 * @Version: V1.0
 */
@Data
@TableName("military_plan")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="military_plan对象", description="方案表")
public class MilitaryPlan {

	/**ID*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "ID")
	private java.lang.String id;
	/**方案名称*/
	@Excel(name = "方案名称", width = 15)
    @ApiModelProperty(value = "方案名称")
	private java.lang.String name;
	/**目标*/
	@Excel(name = "目标", width = 15)
    @ApiModelProperty(value = "目标")
	private java.lang.String target;
	/**周期/天*/
	@Excel(name = "周期/天", width = 15)
    @ApiModelProperty(value = "周期/天")
	private java.lang.Integer cycle;
	/**方案类型：1. 组训方案（原名运动处方）、2. 体训方案、3. 训练班长方案*/
	@Excel(name = "方案类型：1. 组训方案（原名运动处方）、2. 体训方案、3. 训练班长方案", width = 15)
    @ApiModelProperty(value = "方案类型：1. 组训方案（原名运动处方）、2. 体训方案、3. 训练班长方案")
    @Dict(dicCode = "plan_type")
	private java.lang.Integer type;

	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**createBy*/
	@Excel(name = "createBy", width = 15)
    @ApiModelProperty(value = "createBy")
	private java.lang.String createBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private java.lang.String updateBy;
}
