package com.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.crm.common.aop.Log;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.entity.ContractProduct;
import com.crm.enums.BusinessType;
import com.crm.query.ContractQuery;
import com.crm.query.CustomerTrendQuery;
import com.crm.service.ContractProductService;
import com.crm.service.ContractService;
import com.crm.vo.ContractVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Tag(name = "合同管理")
@RestController
@RequestMapping("contract")
@AllArgsConstructor
public class ContractController {
    private final ContractService contractService;
    private final ContractProductService contractProductService;

    @PostMapping("page")
    @Operation(summary = "合同列表-分页")
    @Log(title = "合同列表-分页", businessType = BusinessType.SELECT)
    public Result<PageResult<ContractVO>> getpage(@RequestBody @Validated ContractQuery contractQuery) {
        return Result.ok(contractService.getPage(contractQuery));
    }

    @PostMapping("saveOrEdit")
    @Operation(summary = "保存或更新合同")
    public Result saveOrUpdate(@RequestBody @Validated ContractVO contractVO) {
        contractService.saveOrUpdate(contractVO);
        return Result.ok("保存成功");
    }

    @PostMapping("getContractProduct")
    @Operation(summary = "合同商品列表")
    public Result<List<ContractProduct>> getContractProduct(@RequestParam Integer contractId) {
        QueryWrapper<ContractProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("c_id", contractId);
        List<ContractProduct> contractProducts = contractProductService.list(queryWrapper);
        return Result.ok(contractProducts);
    }
    @PostMapping("getContractTrendData")
    @Operation(summary = "合同变化趋势数据")
    @Log(title = "合同变化趋势数据", businessType = BusinessType.SELECT)
    public Result<Map<String, List>> getContractTrendData(@RequestBody CustomerTrendQuery query){
        return Result.ok(contractService.getContractTrendData(query));
    }
}
