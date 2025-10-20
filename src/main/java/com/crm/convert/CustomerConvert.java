package com.crm.convert;

import com.crm.entity.Customer;
import com.crm.vo.CustomerVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CustomerConvert {
    CustomerConvert INSTANCE = Mappers.getMapper(CustomerConvert.class);
    Customer convert(CustomerVO customerVO);

    // 可能还需要 Entity 到 VO 的转换方法
    CustomerVO convert(Customer customer);

    // 批量转换方法（如果需要）
    List<Customer> convert(List<CustomerVO> customerVOs);
}
