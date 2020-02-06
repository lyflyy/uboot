package org.uboot.modules.system.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.uboot.common.system.vo.DictModel;
import org.uboot.modules.system.entity.SysDict;
import org.uboot.modules.system.entity.SysDictItem;

import java.io.Serializable;
import java.util.List;

@Data
public class SysDictModel extends SysDict implements Serializable {

    /**
     * 主键
     */
    private List<DictModel> dictModels;

}
