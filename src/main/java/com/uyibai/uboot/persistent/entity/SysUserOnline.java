package com.uyibai.uboot.persistent.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

import com.uyibai.uboot.common.domain.OnlineSession;
import com.uyibai.uboot.common.entity.BaseEntity;
import com.uyibai.uboot.common.entity.OnlineStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.shiro.session.mgt.SimpleSession;

/**
 * <p>
 * 在线用户记录
 * </p>
 *
 * @author LiYang [lyflyyvip@163.com]
 * @since 2020-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="SysUserOnline对象", description="在线用户记录")
public class SysUserOnline extends SimpleSession {

private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户会话id")
    @TableId("sessionId")
    private String sessionId;

    @ApiModelProperty(value = "登录账号")
    private String loginName;

    @ApiModelProperty(value = "登录IP地址")
    private String ipaddr;

    @ApiModelProperty(value = "登录地点")
    private String loginLocation;

    @ApiModelProperty(value = "浏览器类型")
    private String browser;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "在线状态on_line在线off_line离线")
    private OnlineStatus status;

    @ApiModelProperty(value = "session创建时间")
    private LocalDateTime startTimestamp;

    @ApiModelProperty(value = "session最后访问时间")
    private LocalDateTime lastAccessTime;

    @ApiModelProperty(value = "超时时间，单位为分钟")
    private Integer expireTime;

    public OnlineSession getSession()
    {
        return session;
    }

    /** 备份的当前用户会话 */
    private OnlineSession session;


}
