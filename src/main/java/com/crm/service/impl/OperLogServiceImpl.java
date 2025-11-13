package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.result.PageResult;
import com.crm.entity.OperLog;
import com.crm.mapper.OperLogMapper;
import com.crm.query.OperLogQuery;
import com.crm.service.OperLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.utils.AddressUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 操作日志记录 服务实现类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Service
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {

    @Override
    public PageResult<OperLog> page(OperLogQuery query) {
        Page<OperLog> page = new Page<>(query.getPage(), query.getLimit());

        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(query.getOperName()),OperLog::getOperName,query.getOperName());
        wrapper.eq(StringUtils.isNotBlank(query.getOperUrl()),OperLog::getOperUrl,query.getOperUrl());
        if (query.getOperTime()!= null && !query.getOperTime().isEmpty()){
            wrapper.between(OperLog::getOperTime,query.getOperTime().get(0),query.getOperTime().get(1));
        }
        wrapper.orderByDesc(OperLog::getOperTime);
        Page<OperLog> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords(),result.getTotal());
    }

    @Override
    public void recordOperLog(OperLog operLog) {
        operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
        operLog.setOperTime(LocalDateTime.now());
        this.save(operLog);
    }
}
