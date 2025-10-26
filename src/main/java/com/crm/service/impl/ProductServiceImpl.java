package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.entity.Product;
import com.crm.enums.ProductOperateEnum;
import com.crm.mapper.ProductMapper;
import com.crm.query.ProductQuery;
import com.crm.query.ProductTimingQuery;
import com.crm.security.utils.DynamicTaskManager;
import com.crm.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author vact
 * @since 2025-10-12
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Override
    public PageResult<Product> getPage(ProductQuery query) {
        Page<Product> page = new Page<>(query.getPage(), query.getLimit());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(query.getName())) {
            wrapper.like(Product::getName, query.getName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Product::getStatus, query.getStatus());
        }
        Page<Product> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords(), page.getTotal());
    }
    @Override
    public void saveOrUpdateProduct(Product product) {
        // 1、查询新增/修改的商品名称是否已经存在，如果存在直接抛出异常
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>().eq(Product::getName, product.getName());
        if (product.getId() == null) {
            List<Product> products = baseMapper.selectList(wrapper);
            if (!products.isEmpty()) {
                throw new ServerException("商品名称已存在");
            }
            // 2、新增商品
            baseMapper.insert(product);
        } else {
            Product existingProduct = baseMapper.selectById(product.getId());
            if (existingProduct == null) {
                throw new ServerException("商品不存在");
            }
            wrapper.ne(Product::getId, product.getId());
            List<Product> products = baseMapper.selectList(wrapper);
            if (!products.isEmpty()) {
                throw new ServerException("商品名称已存在");
            }
            // 3、修改商品
            baseMapper.updateById(product);
        }
    }
    private final DynamicTaskManager taskManager;

    @Override
    @Transactional
    public void handleTimingOperation(ProductTimingQuery dto) {
        // 1. 解析前端参数，匹配操作枚举
        ProductOperateEnum operateEnum = ProductOperateEnum.getByFrontCode(dto.getOperateType());
        Integer productId = dto.getProductId();
        LocalDateTime executeTime = dto.getExecuteTime();

        // 2. 计算延迟时间（当前时间到执行时间的毫秒数，必须为正数）
        long delay = Duration.between(LocalDateTime.now(), executeTime).toMillis();
        if (delay <= 0) {
            throw new ServerException("定时时间必须晚于当前时间");
        }

        // 3. 生成任务唯一标识（避免重复提交，格式："product_商品ID_操作类型"）
        String taskId = "product_" + productId + "_" + operateEnum.getCode();

        // 4. 构建定时任务逻辑（到点执行的操作）
        Runnable task = () -> {
            try {
                // 执行状态修改（加事务确保原子性）
                updateProductStatus(productId, operateEnum,executeTime);
            } catch (Exception e) {
                log.error("定时任务执行失败，taskId:{}", taskId, e);
            }
        };

        // 5. 提交定时任务到管理器
        taskManager.submitTask(taskId, delay, task);

        // 6. 若为下架操作，提前记录下架时间到商品表（核心需求）
        if (operateEnum == ProductOperateEnum.OFFLINE) {
            Product product = new Product();
            product.setId(productId);
            product.setOfflineTime(executeTime); // 记录定时下架时间
            product.setOnlineTime(null);
            baseMapper.updateById(product);
        }
    }

    /**
     * 定时任务执行时：修改商品状态
     */
    private void updateProductStatus(Integer productId, ProductOperateEnum operateEnum,LocalDateTime executeTime) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(operateEnum.getTargetStatus()); // 设置目标状态

        // 若为上架操作，清空下架时间（可选）
        if (operateEnum == ProductOperateEnum.ONLINE) {
            product.setOnlineTime(executeTime);
            product.setOfflineTime(null);
        }

        int updateCount = baseMapper.updateById(product);
        if (updateCount == 0) {
            throw new ServerException("商品不存在，无法执行" + operateEnum.getDesc());
        }
        log.info("商品定时操作执行成功，productId:{}, 操作:{}", productId, operateEnum.getDesc());
    }

}
