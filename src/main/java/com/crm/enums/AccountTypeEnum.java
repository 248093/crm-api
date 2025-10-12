package com.crm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountTypeEnum {
    /**
     * 停用
     */
    A_PLATFORM(0, "A平台"),
    /**
     * 正常
     */
    B_PLATFORM(1, "B平台");

    private final int value;
    private final String name;
}
