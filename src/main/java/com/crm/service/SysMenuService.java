package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.entity.SysMenu;
import com.crm.query.SysMenuQuery;
import com.crm.security.user.ManagerDetail;
import com.crm.vo.SysMenuVO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface SysMenuService extends IService<SysMenu> {
    /**
     * 用户菜单列表
     *
     * @param type 菜单类型
     */
    List<SysMenuVO> getManagerMenuList(ManagerDetail manager, String type);

    Set<String> getManagerAuthority(ManagerDetail manager);

    /**
     * 菜单列表
     *
     * @param query 菜单筛选
     */
    List<SysMenuVO> getMenuList(SysMenuQuery query);

    SysMenuVO infoById(Integer id);

    void save(SysMenuVO vo);


    void update(SysMenuVO vo);

    void delete(Integer id);

    List<SysMenu> getFormMenuList();

    List<SysMenuVO> getRoleFormMenuList();
    /**
     * 获取子菜单的数量
     * @param pid  父菜单ID
     */
    Long getSubMenuCount(Integer pid);
}
