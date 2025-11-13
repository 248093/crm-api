package com.crm.service.impl;


import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.ContractConvert;
import com.crm.entity.*;
import com.crm.mapper.ContractMapper;
import com.crm.mapper.ContractProductMapper;
import com.crm.mapper.ProductMapper;
import com.crm.query.ContractQuery;
import com.crm.query.CustomerQuery;
import com.crm.query.CustomerTrendQuery;
import com.crm.security.user.SecurityUser;
import com.crm.service.ContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.utils.DateUtils;
import com.crm.vo.*;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.crm.utils.DateUtils.*;
import static com.crm.utils.NumberUtils.generateContractNumber;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Service
@Slf4j
@AllArgsConstructor
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {
    private final ContractMapper contractMapper;
    private final ContractProductMapper contractProductMapper; // 注入 ContractProductMapper
    private final ProductMapper productMapper;

    @Override
    public PageResult<ContractVO> getPage(ContractQuery query) {
        Page<ContractVO> page = new Page<>();
        MPJLambdaWrapper<Contract> wrapper = new MPJLambdaWrapper<>();
        if (StringUtils.isNotBlank(query.getName())) {
            wrapper.like(Contract::getName, query.getName());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(Contract::getCustomerId, query.getCustomerId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Contract::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Contract::getCreateTime);
        // 只查询目前登录的员工签署的合同列表
        Integer managerId = SecurityUser.getManagerId();
        wrapper.selectAll(Contract.class)
                .selectAs(Customer::getName, ContractVO::getCustomerName)
                .leftJoin(Customer.class, Customer::getId, Contract::getCustomerId)
                .eq(Contract::getOwnerId, managerId);

        Page<ContractVO> result = baseMapper.selectJoinPage(page, ContractVO.class, wrapper);
        // 查询合同签署的商品信息
        if (!result.getRecords().isEmpty()) {
            result.getRecords().forEach(contractVO -> {
                // 使用 ContractProductMapper 查询合同关联的商品信息
                List<ContractProduct> contractProducts = contractProductMapper.selectList(
                        new LambdaQueryWrapper<ContractProduct>().eq(ContractProduct::getCId, contractVO.getId())
                );
            });
        }

        return new PageResult<>(result.getRecords(), result.getTotal());
    }
    @Override
    @Transactional
    public void saveOrUpdate(ContractVO contractVO) {
        Integer contractId = contractVO.getId();

        // 创建合同实体对象
        Contract contract = new Contract();
        contract.setId(contractId);
        contract.setName(contractVO.getName());
        contract.setCustomerId(contractVO.getCustomerId());
        contract.setAmount(contractVO.getAmount());
        contract.setReceivedAmount(contractVO.getReceivedAmount() != null ? contractVO.getReceivedAmount() : BigDecimal.ZERO);
        contract.setStatus(contractVO.getStatus()==null?0:contractVO.getStatus());
        contract.setStartTime(LocalDate.from(contractVO.getStartTime().atStartOfDay()));
        contract.setEndTime(LocalDate.from(contractVO.getEndTime().atStartOfDay()));
        contract.setSignTime(LocalDate.from(contractVO.getSignTime().atStartOfDay()));
        contract.setCustomerId(contractVO.getCustomerId());
        String remarkStr = contractVO.getRemark();
        if (StringUtils.isNotBlank(remarkStr)) {
            try {
                contract.setRemark(Integer.valueOf(remarkStr));
            } catch (NumberFormatException e) {
                throw new ServerException("备注必须输入数字类型");
            }
        } else {
            contract.setRemark(null);
        }

        // 如果是新增合同，设置创建者信息和编号
        if (contractId == null) {
            contract.setNumber(UUID.randomUUID().toString());
            contract.setCreaterId(SecurityUser.getManagerId());
            contract.setCreateTime(LocalDateTime.now());
            contract.setOwnerId(SecurityUser.getManagerId());
        } else {
            // 如果是更新，保留原有编号
            Contract existingContract = this.getById(contractId);
            if (existingContract != null) {
                contract.setNumber(existingContract.getNumber());
            }
        }

        // 保存或更新合同
        this.saveOrUpdate(contract);

        // 如果是新增合同，获取生成的合同ID
        if (contractId == null) {
            contractId = contract.getId();
        }

        // 处理合同商品关联
        handleContractProducts(contractId, contractVO.getProducts());
    }


    public void handleContractProducts(Integer contractId, List<ProductVO> newProductList) {
        if (newProductList == null) return;

        // 查询当前合同下所有已存在的商品
        List<ContractProduct> oldProducts = contractProductMapper.selectList(
                new LambdaQueryWrapper<ContractProduct>().eq(ContractProduct::getCId, contractId)
        );
        log.info("旧商品：{}", oldProducts);

        // 将旧商品列表转换为Map，便于快速查找
        Map<Integer, ContractProduct> oldProductMap = oldProducts.stream()
                .collect(Collectors.toMap(ContractProduct::getPId, Function.identity()));

        // === 1. 处理新增和更新的商品 ===
        for (ProductVO newProduct : newProductList) {
            Integer pId = newProduct.getPId();
            Integer count = newProduct.getCount();

            if (oldProductMap.containsKey(pId)) {
                // 商品已存在，进行更新操作
                ContractProduct existingProduct = oldProductMap.get(pId);
                Product product = checkAndGetProduct(pId, 0);

                int diff = count - existingProduct.getCount();

                // 库存调整
                if (diff > 0) {
                    decreaseStock(product, diff);
                } else if (diff < 0) {
                    increaseStock(product, -diff);
                }

                // 更新合同商品信息
                existingProduct.setCount(count);
                existingProduct.setPrice(product.getPrice());
                existingProduct.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(count)));
                contractProductMapper.updateById(existingProduct);
                log.info("更新商品：pId={}, count={}", pId, count);
            } else {
                // 商品不存在，进行新增操作
                Product product = checkAndGetProduct(pId, count);
                decreaseStock(product, count);
                ContractProduct cp = buildContractProduct(contractId, product, count);
                contractProductMapper.insert(cp);
                log.info("新增商品：pId={}, count={}", pId, count);
            }
        }

        // === 2. 删除商品（存在于旧商品列表但不在新商品列表中的商品）===
        Set<Integer> newProductIds = newProductList.stream()
                .map(ProductVO::getPId)
                .collect(Collectors.toSet());

        List<ContractProduct> removedProducts = oldProducts.stream()
                .filter(op -> !newProductIds.contains(op.getPId()))
                .toList();

        for (ContractProduct removedProduct : removedProducts) {
            Product product = productMapper.selectById(removedProduct.getPId());
            if (product != null) {
                increaseStock(product, removedProduct.getCount());
            }
            contractProductMapper.deleteById(removedProduct.getId());
            log.info("删除商品：id={}, pId={}", removedProduct.getId(), removedProduct.getPId());
        }
    }

    private Product checkAndGetProduct(Integer productId, int requiredCount) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在，ID: " + productId);
        }
        if (product.getStock() < requiredCount) {
            throw new RuntimeException("商品库存不足，ID: " + productId);
        }
        return product;
    }
    private void decreaseStock(Product product, int count) {
        product.setStock(product.getStock() - count);
        productMapper.updateById(product);
    }

    private void increaseStock(Product product, int count) {
        product.setStock(product.getStock() + count);
        productMapper.updateById(product);
    }
    private ContractProduct buildContractProduct(Integer contractId, Product product, Integer count) {
        ContractProduct cp = new ContractProduct();
        cp.setCId(contractId);
        cp.setPId(product.getId());
        cp.setPName(product.getName());
        cp.setCount(count);
        cp.setPrice(product.getPrice());
        cp.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(count)));
        return cp;
    }
    @Override
    public Map<String, List> getContractTrendData(CustomerTrendQuery query) {
        List<String> timeList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>(); // 新增：用于存储金额数据

        List<ContractTrendVO> tradeStatistics;

        if ("day".equals(query.getTransactionType())){
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = now.truncatedTo(ChronoUnit.SECONDS);
            LocalDateTime startTime = now.withHour(0).withSecond(0).truncatedTo(ChronoUnit.SECONDS);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<String> timeRange = new ArrayList<>();
            timeRange.add(formatter.format(startTime));
            timeRange.add(formatter.format(localDateTime));
            timeList = getHourData(timeList);
            query.setTimeRange(timeRange);
            tradeStatistics = baseMapper.getTradeStatistics(query);
        } else if ("mothrange".equals(query.getTransactionType())) {
            query.setTimeFormat("%Y-%m");
            timeList = getMonthInRange(query.getTimeRange().get(0), query.getTimeRange().get(1));
            tradeStatistics = baseMapper.getTradeStatisticsByDay(query);
        } else if ("week".equals(query.getTransactionType())) {
            timeList = getWeekInRange(query.getTimeRange().get(0), query.getTimeRange().get(1));
            tradeStatistics = baseMapper.getTradeStatisticsByWeek(query);
        }else {
            query.setTimeFormat("%Y-%m-%d");
            timeList = DateUtils.getDatesInRange(query.getTimeRange().get(0), query.getTimeRange().get(1));
            tradeStatistics = baseMapper.getTradeStatisticsByDay(query);
        }

        List<ContractTrendVO> finalTradeStatistics = tradeStatistics;
        timeList.forEach(item -> {
            ContractTrendVO statisticsVO = finalTradeStatistics.stream().filter(vo -> {
                        if ("day".equals(query.getTransactionType())){
                            return item.substring(0,2).equals(vo.getTradeTime().substring(0,2));
                        }else {
                            return item.equals(vo.getTradeTime());
                        }
                    })
                    .findFirst()
                    .orElse(null);

            if (statisticsVO != null){
                countList.add(statisticsVO.getTradeCount());
                amountList.add(statisticsVO.getTradeAmount() != null ? BigDecimal.valueOf(statisticsVO.getTradeAmount()) : BigDecimal.ZERO); // 添加金额数据
            }else {
                countList.add(0);
                amountList.add(BigDecimal.ZERO); // 添加默认金额数据
            }
        });

        Map<String, List> result = new HashMap<>();
        result.put("timeList", timeList);
        result.put("countList", countList);
        result.put("amountList", amountList); // 新增：返回金额列表
        return result;
    }

}
