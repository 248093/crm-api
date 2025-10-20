package com.crm.service;


import com.crm.common.result.PageResult;
import com.crm.entity.Customer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.CustomerQuery;
import com.crm.query.IdQuery;
import com.crm.vo.CustomerVO;
import jakarta.servlet.http.HttpServletResponse;

import java.net.http.HttpResponse;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface CustomerService extends IService<Customer> {
    //分页
    PageResult<CustomerVO> getPage(CustomerQuery query);
    //导出
    void exportCustomer(CustomerQuery query, HttpServletResponse response);

    void saveOrUpdate(CustomerVO customerVO);
    void customerToPublicPool(IdQuery idQuery);
    void publicPoolToPrivate(IdQuery idQuery);
    /**
     * 删除客户信息
     * @param ids
     */
    void removeCustomer(List<Integer> ids);
}