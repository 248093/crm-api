package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.entity.Department;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.IdQuery;
import com.crm.query.DepartmentQuery;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface DepartmentService extends IService<Department> {
    /**
     * 部门分类列表
     * @param query
     * @return
     */
    PageResult<Department> getPage(DepartmentQuery query);
    /**
     * 部门列表 - 不分页
     *
     * @return
     */
    List<Department> getList();
    /**
     * 保存或编辑部门
     * @param department
     */
    void saveOrEditDepartment(Department department);
    /**
     * 删除部门
     * @param query
     */
    void removeDepartment(IdQuery query);
}