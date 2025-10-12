package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.common.exception.ServerException;
import com.crm.entity.SysManagerRole;
import com.crm.mapper.SysManagerRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.service.SysManagerRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户角色关系 服务实现类
 * </p>
 *

 */
@Service
public class SysManagerRoleServiceImpl extends ServiceImpl<SysManagerRoleMapper, SysManagerRole> implements SysManagerRoleService {
    @Override
    public void saveOrUpdate(Integer managerId, Integer roleId) {
        SysManagerRole sysManagerRole = baseMapper.selectOne(new LambdaQueryWrapper<SysManagerRole>()
                .eq(SysManagerRole::getManagerId, managerId));
        if (sysManagerRole == null) {
            sysManagerRole = new SysManagerRole();
            sysManagerRole.setManagerId(managerId);
        }
        sysManagerRole.setRoleId(roleId);
        saveOrUpdate(sysManagerRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByManagerId(List<Integer> idList) {
        baseMapper.delete(new LambdaQueryWrapper<SysManagerRole>()
                .in(SysManagerRole::getManagerId, idList));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoleIdList(List<Integer> roleIdList) {
        remove(new LambdaQueryWrapper<SysManagerRole>().in(SysManagerRole::getRoleId, roleIdList));
    }

    @Override
    public SysManagerRole getByManagerId(Integer managerId) {
        SysManagerRole sysManagerRole = baseMapper.selectOne(new LambdaQueryWrapper<SysManagerRole>()
                .eq(SysManagerRole::getManagerId, managerId));
        if (sysManagerRole == null) {
            throw new ServerException("该管理员暂未绑定角色");
        }
        return sysManagerRole;
    }
}
