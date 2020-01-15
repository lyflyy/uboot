package com.uyibai.uboot.common.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2017/4/23  上午10:21
 */
@Data
@Accessors(chain = true)
public class BaseEntity implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedTime;
    /**
     * 是否删除,0:未删除,1:删除
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 返回前端序列化问题：@JsonSerialize(using=ToStringSerializer.class)
     *
     * @return
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public Long getId() {
        return id;
    }

    public static final String ID = "id";
    public static final String CREATED_TIME = "createdTime";
    public static final String UPDATED_TIME = "updatedTime";
    public static final String DELETED = "deleted";
}
