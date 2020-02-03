package org.uboot.modules.system.entity;

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

/**
 * @Description: 租户-用户
 * @Author: jeecg-boot
 * @Date:   2020-02-03
 * @Version: V1.0
 */
@Data
@TableName("sys_tenant_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_tenant_user对象", description="租户-用户")
public class SysTenantUser {

	/**id*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "id")
	private String id;
	/**租户Id*/
	@Excel(name = "租户Id", width = 15)
    @ApiModelProperty(value = "租户Id")
	private String sysTenantId;
	/**用户Id*/
	@Excel(name = "用户Id", width = 15)
    @ApiModelProperty(value = "用户Id")
	private String sysUserId;
}
