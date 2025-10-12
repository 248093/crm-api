package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.common.result.PageResult;
import com.crm.entity.SysRole;
import com.crm.query.SysRoleQuery;
import com.crm.vo.SysRoleVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface SysRoleService extends IService<SysRole> {
    PageResult<SysRoleVO> page(SysRoleQuery query);

    List<SysRoleVO> getList(SysRoleQuery query);

    void save(SysRoleVO vo);

    void update(SysRoleVO vo);

    void delete(List<Integer> idList);
}
