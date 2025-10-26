package com.crm.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductTimingQuery {
    private Integer productId;         // 商品ID（前端传）
    private String operateType;     // 前端传递的操作类型（如 "ONLINE"/"OFFLINE"）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executeTime; // 前端传递的定时执行时间
}