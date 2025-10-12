package com.crm.security.user;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author crm
 */
public class SecurityUser {
    /**
     * 获取用户信息
     */
    public static ManagerDetail getManager() {
        ManagerDetail user;
        try {
            user = (ManagerDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (Exception e){
            return new ManagerDetail();
        }

        return user;
    }

    /**
     * 获取用户ID
     */
    public static Integer getManagerId() {
        return getManager().getId();
    }

}