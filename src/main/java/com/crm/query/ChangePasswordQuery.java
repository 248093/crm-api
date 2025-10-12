package com.crm.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;




@Data
@Schema(description = "修改管理员密码")
public class ChangePasswordQuery {
    @Schema(description = "主键")
    private Integer id;
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
