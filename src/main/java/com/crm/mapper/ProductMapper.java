package com.crm.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface ProductMapper extends BaseMapper<Product> {
    IPage<Product> selectPageVo(Page<Product> page, @Param("name") String name,@Param("status") Integer status);
}
