package com.study.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlterCourseDto extends AddCourseDto{

    //课程id
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
}
