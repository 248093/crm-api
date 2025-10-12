package com.crm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *

 */
@Getter
@Setter
@TableName("sys_menu")
public class SysMenu {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer Id;

    @TableField("parent_id")
    private Integer parentId;

    @TableField("name")
    private String name;

    @TableField("title")
    private String title;

    @TableField("path")
    private String path;


    @TableField("component")
    private String component;

    @TableField("type")
    private String type;

    @TableField("open_type")
    private String openType;

    @TableField("url")
    private String url;

    @TableField("icon")
    private String icon;

    @TableField("auth")
    private String auth;

    @TableField("keep_alive")
    private Integer keepAlive;

    @TableField("sort")
    private Integer sort;

    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleteFlag;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("hide")
    private Integer hide;
}
