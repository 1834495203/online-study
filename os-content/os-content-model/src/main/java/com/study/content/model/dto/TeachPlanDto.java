package com.study.content.model.dto;

import com.study.content.model.po.Teachplan;
import com.study.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TeachPlanDto extends Teachplan {

    //关联的媒体资料信息
    private TeachplanMedia teachplanMedia;

    //子目录
    private List<TeachPlanDto> teachPlanTreeNodes;
}
