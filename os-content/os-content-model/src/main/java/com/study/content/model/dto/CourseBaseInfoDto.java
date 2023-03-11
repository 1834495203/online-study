package com.study.content.model.dto;

import com.study.content.model.po.CourseBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程基本信息dto
 * @author GLaDOS
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseBaseInfoDto extends CourseBase {

    /**
     * 收费规则，对应数据字典
     */
    @ApiModelProperty(value = "收费规则，对应数据字典", required = true)
    private String charge;

    /**
     * 价格
     */
    @ApiModelProperty(value = "价格", required = true)
    private Float price;

    /**
     * 原价
     */
    @ApiModelProperty(value = "原价")
    private Float originalPrice;

    /**
     * 咨询qq
     */
    @ApiModelProperty(value = "咨询qq", required = true)
    private String qq;

    /**
     * 微信
     */
    @ApiModelProperty(value = "微信", required = true)
    private String wechat;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话", required = true)
    private String phone;

    /**
     * 有效期天数
     */
    @ApiModelProperty(value = "有效期天数")
    private Integer validDays;

    /**
     * 大分类名称
     */
    @ApiModelProperty(value = "大分类名称", required = true)
    private String mtName;

    /**
     * 小分类名称
     */
    @ApiModelProperty(value = "小分类名称", required = true)
    private String stName;

}
