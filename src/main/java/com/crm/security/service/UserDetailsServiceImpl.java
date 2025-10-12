package com.crm.security.service;

import com.crm.entity.SysManager;
import com.crm.mapper.SysManagerMapper;
import com.crm.service.SysManagerDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 账号登录 UserDetailsService
 *
 * @author 阿沐 babamu@126.com
 * <a href="https://maku.net">MAKU</a>
 */
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final SysManagerDetailsService sysManagerDetailsService;
    private final SysManagerMapper sysManagerMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysManager sysManager = sysManagerMapper.getByAccount(username);
        if (sysManager == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return sysManagerDetailsService.getManagerDetails(sysManager);
    }
}
