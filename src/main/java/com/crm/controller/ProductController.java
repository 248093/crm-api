package com.crm.controller;

import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.entity.Department;
import com.crm.entity.Product;
import com.crm.enums.ProductOperateEnum;
import com.crm.query.ProductQuery;
import com.crm.query.ProductTimingQuery;
import com.crm.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author vact
 * @since 2025-10-12
 */
@Api(tags = "商品管理")
@RestController
@RequestMapping("product")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("page")
    @Operation(summary = "分页查询")
    public Result<PageResult<Product>> getPage(@RequestBody @Validated ProductQuery query) {
        return Result.ok(productService.getPage(query));
    }

    @PostMapping("saveOrEdit")
    @Operation(summary = "保存或编辑部门")
    public Result saveOrEditDepartment(@RequestBody Product product) {
        productService.saveOrUpdateProduct(product);
        return Result.ok();
    }


    // 接收前端定时上下架请求
    @PostMapping("/timing")
    public Result<?> timing(@RequestBody ProductTimingQuery productTimingQuery) {
        productService.handleTimingOperation(productTimingQuery);
        return Result.ok("定时操作设置成功");
    }
}

