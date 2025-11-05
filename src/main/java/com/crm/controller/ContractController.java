package com.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.entity.ContractProduct;
import com.crm.query.ContractQuery;
import com.crm.service.ContractProductService;
import com.crm.service.ContractService;
import com.crm.service.ProductService;
import com.crm.vo.ContractVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import java.util.List;

@Tag(name = "合同管理")
@RestController
@RequestMapping("contract")
@AllArgsConstructor
public class ContractController {
    private final ContractService contractService;
    private final ContractProductService contractProductService;
    @PostMapping("page")
    @Operation(summary = "合同列表-分页")
    public Result<PageResult<ContractVO>> getPage(@RequestBody @Validated ContractQuery contractQuery) {
        return Result.ok(contractService.getPage(contractQuery));
    }
    @PostMapping("getContractProduct")
    @Operation(summary = "合同商品列表")
    public Result<List<ContractProduct>> getContractProduct(@RequestParam Integer contractId) {
        QueryWrapper<ContractProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("c_id", contractId);
        List<ContractProduct> contractProducts = contractProductService.list(queryWrapper);
        return Result.ok(contractProducts);
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "保存或修改合同")
    public Result<String> saveOrEdit(@RequestBody @Validated ContractVO contractVO) {
        contractService.saveOrUpdate(contractVO);
        return Result.ok("保存成功");
    }
}