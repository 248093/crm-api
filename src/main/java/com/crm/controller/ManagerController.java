package com.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.common.result.Result;
import com.crm.entity.SysManager;
import com.crm.service.SysManagerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户管理 前端控制器
 * </p>
 *
 * @author vact
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/crm/manager")
@AllArgsConstructor
public class ManagerController {
    private final SysManagerService sysManagerService;
    @PostMapping("listByDept")
    @Operation(summary = "根据部门ID获取销售人员列表")
    public Result<List<SysManager>> getListByDept(@RequestBody List<Integer> deptIds) {
        LambdaQueryWrapper<SysManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysManager::getDepartId, deptIds)
                .eq(SysManager::getStatus, 1) // 只查询启用状态的用户
                .eq(SysManager::getDeleteFlag, 0); // 未删除的用户

        List<SysManager> managers = sysManagerService.list(queryWrapper);
        return Result.ok(managers);
    }
}
