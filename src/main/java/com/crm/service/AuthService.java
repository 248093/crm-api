package com.crm.service;


import com.crm.vo.SysAccountLoginVO;
import com.crm.vo.SysTokenVO;

/**
 * 认证服务
 *
 */
public interface AuthService {

    /**
     * 使用账号密码登录
     * @param params
     * @return
     */
    SysTokenVO loginByAccount(SysAccountLoginVO params);

    /**
     * 退出登录
     *
     * @param accessToken accessToken
     */
    void logout(String accessToken);

}
