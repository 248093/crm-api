package com.crm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.crm.common.constant.Constant;
import com.crm.common.exception.ServerException;
import com.crm.convert.SysMenuConvert;
import com.crm.entity.SysMenu;
import com.crm.enums.MenuTypeEnum;
import com.crm.mapper.SysMenuMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.query.SysMenuQuery;
import com.crm.security.user.ManagerDetail;
import com.crm.service.SysMenuService;
import com.crm.service.SysRoleMenuService;
import com.crm.utils.TreeUtils;
import com.crm.vo.SysMenuVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *

 */
@Service
@AllArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    private final SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenuVO> getManagerMenuList(ManagerDetail manager, String type) {
        List<SysMenu> menuList;
        // 系统管理员，拥有最高权限
        if ("admin".equals(manager.getAccount())) {
            menuList = baseMapper.getMenuList(type,false);
        } else {
            menuList = baseMapper.getManagerMenuList(manager.getId(), type,false);
        }

        return TreeUtils.build(SysMenuConvert.INSTANCE.convertList(menuList));
    }

    @Override
    public Set<String> getManagerAuthority(ManagerDetail manager) {
        // 系统管理员，拥有最高权限
        List<String> authorityList;
        authorityList = baseMapper.getManagerAuthorityList(manager.getId());

        // 用户权限列表
        Set<String> permsSet = new HashSet<>();
        for (String authority : authorityList) {
            if (StrUtil.isBlank(authority)) {
                continue;
            }
            permsSet.addAll(Arrays.asList(authority.trim().split(",")));
        }

        return permsSet;
    }

    @Override
    public List<SysMenuVO> getMenuList(SysMenuQuery query) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(query.getTitle())){
            wrapper.like(SysMenu::getTitle,query.getTitle());
        }
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menuList = baseMapper.selectList(wrapper);
        return TreeUtils.build(SysMenuConvert.INSTANCE.convertList(menuList), Constant.ROOT);
    }


    @Override
    public SysMenuVO infoById(Integer id) {
        SysMenu entity = baseMapper.selectById(id);
        SysMenuVO vo = SysMenuConvert.INSTANCE.convert(entity);

        // 获取上级菜单名称
        if (!Constant.ROOT.equals(entity.getParentId())) {
            SysMenu parentEntity = baseMapper.selectById(entity.getParentId());
            vo.setParentName(parentEntity.getName());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysMenuVO vo) {
        SysMenu entity = SysMenuConvert.INSTANCE.convert(vo);
        // 保存菜单
        baseMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysMenuVO vo) {
        SysMenu entity = SysMenuConvert.INSTANCE.convert(vo);
        // 上级菜单不能为自己
        if (entity.getId().equals(entity.getParentId())) {
            throw new ServerException("上级菜单不能为自己");
        }
        // 更新菜单
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        long count = count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        if (count > 0) {
            throw new ServerException("请先删除子菜单");
        }
        // 删除菜单
        removeById(id);

        // 删除角色菜单关系
        sysRoleMenuService.deleteByMenuId(id);
    }

    @Override
    public List<SysMenu> getFormMenuList() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenu::getType, MenuTypeEnum.MENU.name(), MenuTypeEnum.MENU_DIR.name());
        List<SysMenuVO> treeMenu = TreeUtils.build(SysMenuConvert.INSTANCE.convertList(baseMapper.selectList(wrapper)), Constant.ROOT);
        List<SysMenu> menuList = new ArrayList<>();
        SysMenu sysMenu = new SysMenu();
        sysMenu.setId(0);
        sysMenu.setTitle("根节点");
        menuList.add(sysMenu);
        for (SysMenuVO menu : treeMenu) {
            menuList.add(SysMenuConvert.INSTANCE.convert(menu));
            for (int i = 0; i < menu.getChildren().size(); i++) {
                SysMenuVO item = menu.getChildren().get(i);
                if (i < menu.getChildren().size() - 1){
                    item.setTitle("      " + item.getTitle());
                } else {
                    item.setTitle("      " + item.getTitle());
                }
                menuList.add(SysMenuConvert.INSTANCE.convert(item));
            }
        }
        return menuList;
    }

    @Override
    public List<SysMenuVO> getRoleFormMenuList() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper();
//        // 排除菜单管理 只给开发者配置
//        wrapper.ne(SysMenu::getTitle,"菜单管理");
//        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menuList = baseMapper.selectList(wrapper);
        return TreeUtils.build(SysMenuConvert.INSTANCE.convertList(menuList), Constant.ROOT);
    }

    @Override
    public Long getSubMenuCount(Integer pid) {
        return count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, pid));
    }
}
