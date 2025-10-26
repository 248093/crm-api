package com.crm.enums;

import lombok.Getter;

/**
 * 商品操作类型枚举（直接对应前端传递的 operateType）
 * code: 前端传递的操作标识（如 ONLINE/OFFLINE）
 * targetStatus: 该操作对应的目标状态值（直接关联数据库 status 字段）
 * desc: 操作描述（用于日志/提示）
 */
@Getter
public enum ProductOperateEnum {
    // 枚举项：前端操作标识 → 目标状态值 → 描述
    ONLINE("ONLINE", 1, "定时上架"),  // 前端传 "ONLINE" → 目标状态 1（已上架）
    OFFLINE("OFFLINE", 2, "定时下架"); // 前端传 "OFFLINE" → 目标状态 2（已下架）

    // 前端传递的操作 code（如 "ONLINE"，与枚举名一致，也可自定义）
    private final String code;
    // 该操作对应的目标状态值（直接映射到 t_product 表的 status 字段）
    private final Integer targetStatus;
    // 操作描述（用于日志、接口提示）
    private final String desc;

    // 构造方法：直接关联“前端操作 code + 目标状态值 + 描述”
    ProductOperateEnum(String code, Integer targetStatus, String desc) {
        this.code = code;
        this.targetStatus = targetStatus;
        this.desc = desc;
    }

    /**
     * 核心工具方法：通过前端传递的 code 直接获取枚举（无需额外转换）
     * @param frontCode 前端传递的 operateType（如 "ONLINE"）
     * @return 对应的枚举项
     */
    public static ProductOperateEnum getByFrontCode(String frontCode) {
        // 遍历枚举，匹配前端传递的 code
        for (ProductOperateEnum operate : values()) {
            if (operate.getCode().equals(frontCode)) {
                return operate;
            }
        }
        // 匹配不到时抛异常，快速发现前端传参错误
        throw new IllegalArgumentException("无效的前端操作类型：" + frontCode);
    }
}