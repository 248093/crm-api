package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.entity.FollowUp;
import com.crm.entity.Lead;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.IdQuery;
import com.crm.query.LeadQuery;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface LeadService extends IService<Lead> {
    PageResult<Lead> getPage(LeadQuery query);

    void saveOrEdit(Lead lead);

    void followLead(FollowUp followUp);

    void convertToCustomer(IdQuery idQuery);
}
