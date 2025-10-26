package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.enums.ProductOperateEnum;
import com.crm.query.ProductQuery;
import com.crm.query.ProductTimingQuery;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author vact
 * @since 2025-10-12
 */
public interface ProductService extends IService<Product> {
    /**
     * 商品列表
     * @param query
     * @return
     */
    PageResult<Product> getPage(ProductQuery query);
    /**
     * 保存或更新商品
     * @param product
     */
    void saveOrUpdateProduct(Product product);

    void handleTimingOperation(ProductTimingQuery productTimingQuery);
}
