package org.uboot.common.system.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: wm_import_log
 * @Author: jeecg-boot
 * @Date:   2020-03-01
 * @Version: V1.0
 */
@Data
@TableName("wm_import_log")
public class WmImportLog implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**id*/
	@TableId(type = IdType.ID_WORKER_STR)
    private java.lang.String id;
	/**兵季id*/
	@Excel(name = "兵季id", width = 15)
    private java.lang.String tenantId;
	/**凭证附件*/
	@Excel(name = "凭证附件", width = 15)
    private java.lang.String fileUrls;
	/**excel文件*/
	@Excel(name = "excel文件", width = 15)
    private java.lang.String excelUrl;
	/**excel文件*/
	@Excel(name = "excel文件", width = 15)
    private java.lang.String excelName;
	/**类型：1 训练数据导入*/
	@Excel(name = "类型：1 训练数据导入", width = 15)
    private String type;
	/**
	 * 导入数据条数
	 */
	private Integer dataSize;

	/**createBy*/
	@Excel(name = "createBy", width = 15)
    private java.lang.String createBy;
	/**createTime*/
	@Excel(name = "createTime", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
	/**updateBy*/
	@Excel(name = "updateBy", width = 15)
    private java.lang.String updateBy;
	/**updateTime*/
	@Excel(name = "updateTime", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;
	/**删除状态(0-正常,1-已删除)*/
	@Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
    private java.lang.Integer delFlag;
}
