package org.uboot.common.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 上传的文件信息 vo
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2020/3/7
 */
@Data
public class UploadFileInfoVo {
    /**
     * 需要保存数据库的名字
     */
    private String dbpath;
    /**
     * 原文件名
     */
    private String orName;
    /**
     * 新文件名
     */
    private String newName;
    /**
     * 不带扩展名的 名字
     */
    private String name;
    /**
     * 后缀
     */
    private String ext;

    @JsonIgnore
    private String path;

}
