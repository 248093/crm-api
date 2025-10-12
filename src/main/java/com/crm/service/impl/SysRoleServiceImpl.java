package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.result.PageResult;
import com.crm.convert.SysRoleConvert;
import com.crm.entity.SysRole;
import com.crm.mapper.SysRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.mapper.SysRoleMenuMapper;
import com.crm.query.SysRoleQuery;
import com.crm.service.SysManagerRoleService;
import com.crm.service.SysRoleMenuService;
import com.crm.service.SysRoleService;
import com.crm.vo.SysRoleVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *

 */
@Service
@AllArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    private SysRoleMenuService sysRoleMenuService;
    private SysManagerRoleService sysManagerRoleService;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public PageResult<SysRoleVO> page(SysRoleQuery query) {
        Page<SysRole> page = new Page<>(query.getPage(), query.getLimit());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (query.getName() != null) {
            wrapper.like(SysRole::getName, query.getName());
        }
        wrapper.orderByDesc(SysRole::getCreateTime);
        Page<SysRole> result = baseMapper.selectPage(page, wrapper);
        List<SysRoleVO> list = SysRoleConvert.INSTANCE.convertList(result.getRecords());
        if (list.size() > 0) {
            for (SysRoleVO sysRoleVO : list) {
                List<Integer> menuIdList = sysRoleMenuMapper.getMenuIdList(sysRoleVO.getId());
                sysRoleVO.setMenuIds(menuIdList);

            }

        }
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<SysRoleVO> getList(SysRoleQuery query) {
        List<SysRole> entityList = baseMapper.selectList(new LambdaQueryWrapper<>());
        return SysRoleConvert.INSTANCE.convertList(entityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysRoleVO vo) {
        SysRole entity = SysRoleConvert.INSTANCE.convert(vo);
        // 保存角色
        baseMapper.insert(entity);
        // 保存角色菜单关系
        sysRoleMenuService.saveOrUpdate(entity.getId(), vo.getMenuIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRoleVO vo) {
        SysRole entity = SysRoleConvert.INSTANCE.convert(vo);

        // 更新角色
        updateById(entity);

        // 更新角色菜单关系
        sysRoleMenuService.saveOrUpdate(entity.getId(), vo.getMenuIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Integer> idList) {
        // 删除角色
        removeByIds(idList);

        // 删除用户角色关系
        sysManagerRoleService.deleteByRoleIdList(idList);

        // 删除角色菜单关系
        sysRoleMenuService.deleteByRoleIdList(idList);
    }
}
