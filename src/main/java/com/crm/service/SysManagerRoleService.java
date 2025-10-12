package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.entity.SysManagerRole;

import java.util.List;

/**
 * <p>
 * 用户角色关系 服务类
 * </p>
 *

 */
public interface SysManagerRoleService extends IService<SysManagerRole> {
    /**
     * 保存或修改
     * @param managerId   用户ID
     * @param roleId  角色ID
     */
    void saveOrUpdate(Integer managerId, Integer roleId);
    /**
     * 删除
     * @param idList   用户ID
     */
    void removeByManagerId(List<Integer> idList);

    /**
     * 根据角色id列表，删除用户角色关系
     * @param roleIdList 角色id
     */
    void deleteByRoleIdList(List<Integer> roleIdList);

    /**
     * 根据管理员ID 获取管理员角色信息
     * @param managerId
     * @return
     */
    SysManagerRole getByManagerId(Integer managerId);
}
