package com.crm.controller;

import com.crm.common.result.Result;
import com.crm.entity.SysMenu;
import com.crm.enums.MenuTypeEnum;
import com.crm.query.SysMenuQuery;
import com.crm.security.user.ManagerDetail;
import com.crm.security.user.SecurityUser;
import com.crm.service.SysMenuService;
import com.crm.vo.IdVO;
import com.crm.vo.SysMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  前端控制器
 * </p>
 *

 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/sys/menu")
@AllArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @PostMapping("nav")
    @Operation(summary = "用户菜单")
    public Result<List<SysMenuVO>> nav() {
        ManagerDetail manager = SecurityUser.getManager();
        return Result.ok(sysMenuService.getManagerMenuList(manager, MenuTypeEnum.BUTTON.name()));
    }

    @PostMapping("button")
    @Operation(summary = "用户按钮权限")
    public Result<Set<String>> authority() {
        ManagerDetail manager = SecurityUser.getManager();
        return Result.ok(sysMenuService.getManagerAuthority(manager));
    }

    @PostMapping("list")
    @Operation(summary = "菜单列表")
    @Parameter(name = "type", description = "菜单类型 0：菜单 1：按钮  null：全部")
    public Result<List<SysMenuVO>> list(@RequestBody SysMenuQuery query) {
        List<SysMenuVO> list = sysMenuService.getMenuList(query);
        return Result.ok(list);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    public Result<SysMenuVO> get(@RequestBody IdVO vo) {
        return Result.ok(sysMenuService.infoById(vo.getId()));
    }

    @PostMapping("add")
    @Operation(summary = "保存")
    public Result<String> save(@RequestBody @Valid SysMenuVO vo) {
        sysMenuService.save(vo);
        return Result.ok();
    }

    @PostMapping("edit")
    @Operation(summary = "修改")
    public Result<String> update(@RequestBody @Valid SysMenuVO vo) {
        sysMenuService.update(vo);
        return Result.ok();
    }

    @PostMapping("remove")
    @Operation(summary = "删除")
    public Result<String> delete(@RequestBody IdVO vo) {
        sysMenuService.delete(vo.getId());
        return Result.ok();
    }

    @PostMapping("form")
    @Operation(summary = "表单菜单列表")
    public Result<List<SysMenu>> getFormMenuList() {
        List<SysMenu> list = sysMenuService.getFormMenuList();
        return Result.ok(list);
    }


}