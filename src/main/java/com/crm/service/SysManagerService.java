package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.common.result.PageResult;
import com.crm.entity.SysManager;
import com.crm.query.ChangePasswordQuery;
import com.crm.query.SysManagerQuery;
import com.crm.security.user.ManagerDetail;
import com.crm.vo.SysManagerVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *

 */
public interface SysManagerService extends IService<SysManager> {
    /**
     * 管理员列表
     *
     * @param query
     * @return
     */
    PageResult<SysManagerVO> page(SysManagerQuery query);

    /**
     * 新增管理员
     *
     * @param vo
     */
    void save(SysManagerVO vo);

    /**
     * 修改管理员信息
     *
     * @param vo
     */
    void update(SysManagerVO vo);

    /**
     * 删除管理员信息
     *
     * @param idList
     */
    void delete(List<Integer> idList);

    /**
     * 修改密码
     *
     * @param query
     */

    void changePassword(ChangePasswordQuery query);

    /**
     * 获取管理员信息
     *
     * @param manage
     * @return
     */

    SysManagerVO getManagerInfo(ManagerDetail manage);
}
