package com.crm.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.SysManagerConvert;
import com.crm.entity.SysManager;
import com.crm.mapper.SysManagerMapper;
import com.crm.query.ChangePasswordQuery;
import com.crm.query.SysManagerQuery;
import com.crm.security.user.ManagerDetail;
import com.crm.service.SysManagerRoleService;
import com.crm.service.SysManagerService;
import com.crm.vo.SysManagerVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *

 */
@Service
@AllArgsConstructor
public class SysManagerServiceImpl extends ServiceImpl<SysManagerMapper, SysManager> implements SysManagerService {
    private SysManagerRoleService sysManagerRoleService;

    @Override
    public PageResult<SysManagerVO> page(SysManagerQuery query) {
        Page<SysManagerVO> page = new Page<>(query.getPage(), query.getLimit());
        List<SysManagerVO> list = baseMapper.getManagerPage(page, query);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysManagerVO vo) {
        SysManager entity = SysManagerConvert.INSTANCE.convert(vo);

        // 判断用户名是否存在
        SysManager manager = baseMapper.getByAccount(entity.getAccount());
        if (manager != null) {
            throw new ServerException("此用户名已经存在");
        }
        // 保存用户
        baseMapper.insert(entity);
        sysManagerRoleService.saveOrUpdate(entity.getId(), vo.getRoleId());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysManagerVO vo) {
        SysManager entity = SysManagerConvert.INSTANCE.convert(vo);
        // 判断用户名是否存在
        SysManager manager = baseMapper.getByAccount(entity.getAccount());
        if (manager != null && !manager.getId().equals(entity.getId())) {
            throw new ServerException("此用户名已经存在");
        }
        // 更新用户
        updateById(entity);
        // 更新用户角色关系
        sysManagerRoleService.saveOrUpdate(entity.getId(), vo.getRoleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Integer> idList) {
        // 删除管理员
        removeByIds(idList);
        // 删除用户角色关系
        sysManagerRoleService.removeByManagerId(idList);
    }

    @Override
    public SysManagerVO getManagerInfo(ManagerDetail manage) {
        SysManagerVO sysManagerVO = new SysManagerVO();
        SysManager sysManager = baseMapper.selectById(manage.getId());
        if (sysManager == null) {
            throw new ServerException("管理员不存在");
        }
        sysManagerVO.setId(sysManager.getId());
        sysManagerVO.setAccount(sysManager.getAccount());
        sysManagerVO.setStatus(sysManager.getStatus());
        sysManagerVO.setRoleId(sysManagerRoleService.getByManagerId(manage.getId()).getRoleId());
        sysManagerVO.setCreateTime(sysManager.getCreateTime());
        return sysManagerVO;
    }

    @Override
    public void changePassword(ChangePasswordQuery query) {
        SysManager sysManager = baseMapper.selectById(query.getId());
        if (sysManager == null) {
            throw new ServerException("管理员不存在");
        }
        sysManager.setPassword(query.getPassword());
        updateById(sysManager);
    }
}
