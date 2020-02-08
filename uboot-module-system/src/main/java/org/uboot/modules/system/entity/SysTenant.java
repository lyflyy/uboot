package org.uboot.modules.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
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
 * @Description: 租户
 * @Author: jeecg-boot
 * @Date:   2020-02-03
 * @Version: V1.0
 */
@Data
@TableName("sys_tenant")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_tenant对象", description="租户")
public class SysTenant {

	/**id*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "id")
	private String id;
    /**租户名称*/
    @Excel(name = "租户名称", width = 15)
    @ApiModelProperty(value = "租户名称")
    private String name;

    /**租户编号*/
    @Excel(name = "租户编号", width = 15)
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private String remark;
	/**状态（1归档 0正常）*/
	@Excel(name = "状态（1归档 2正常）", width = 15, dicCode = "tenant_status")
    @Dict(dicCode = "tenant_status")
    @ApiModelProperty(value = "状态（1归档 2正常）")
	private Integer status;
	/**扩展信息*/
	@Excel(name = "扩展信息", width = 15)
    @ApiModelProperty(value = "扩展信息")
	private String ext;
	/**createBy*/
	@Excel(name = "createBy", width = 15)
    @ApiModelProperty(value = "createBy")
	private String createBy;
	/**createTime*/
	@Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
	private Date createTime;
	/**updateBy*/
	@Excel(name = "updateBy", width = 15)
    @ApiModelProperty(value = "updateBy")
	private String updateBy;
	/**updateTime*/
	@Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "updateTime")
	private Date updateTime;

    @Excel(name="删除状态",width=15)
    @Dict(dicCode = "del_flag")
    @TableLogic
    private String delFlag;
}
