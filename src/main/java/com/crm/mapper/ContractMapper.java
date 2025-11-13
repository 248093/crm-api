package com.crm.mapper;

import com.crm.entity.Contract;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.query.CustomerTrendQuery;
import com.crm.vo.ContractTrendVO;
import com.crm.vo.CustomerTrendVO;
import com.github.yulichang.base.MPJBaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface ContractMapper extends MPJBaseMapper<Contract> {
    List<ContractTrendVO> getTradeStatistics(@Param("query") CustomerTrendQuery query);

    List<ContractTrendVO> getTradeStatisticsByDay(@Param("query") CustomerTrendQuery query);

    List<ContractTrendVO> getTradeStatisticsByWeek(@Param("query") CustomerTrendQuery query);

}
