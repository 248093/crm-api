package com.crm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@TableName("t_contract_product")
@AllArgsConstructor
@NoArgsConstructor
public class ContractProduct {
    @ApiModelProperty("主键id")
    @TableField("id")
    private Integer id;
    @ApiModelProperty("产品ID")
    @TableField("p_id")
    private Integer pId;
    @ApiModelProperty("合同id")
    @TableField("c_id")
    private Integer cId;
    @ApiModelProperty("商品名称")
    @TableField("p_name")
    private String pName;
    @ApiModelProperty("商品价格")
    @TableField("price")
    private BigDecimal price;
    @ApiModelProperty("购买商品数量")
    @TableField("count")
    private Integer count;
    @ApiModelProperty("总价格")
    @TableField("total_price")
    private BigDecimal totalPrice;
}