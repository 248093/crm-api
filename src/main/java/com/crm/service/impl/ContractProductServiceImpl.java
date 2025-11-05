package com.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.entity.ContractProduct;
import com.crm.mapper.ContractProductMapper;
import com.crm.service.ContractProductService;
import org.springframework.stereotype.Service;

@Service
public class ContractProductServiceImpl extends ServiceImpl<ContractProductMapper, ContractProduct> implements ContractProductService {
}
