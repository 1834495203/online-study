package com.study.content.service.impl;

import com.study.content.mapper.CourseCategoryMapper;
import com.study.content.model.dto.CourseCategoryTreeDto;
import com.study.content.model.po.CourseCategory;
import com.study.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //得到了根节点下面所有的节点
        List<CourseCategoryTreeDto> treeNodes = courseCategoryMapper.selectTreeNodes(id);

        //定义一个List作为返回的最终数据
        List<CourseCategoryTreeDto> nodes = new ArrayList<>();

        //定义一个map找父节点
        HashMap<String, CourseCategoryTreeDto> nodeMap = new HashMap<>();

        //将数据封装到List中, 只包括了根节点的直接下属节点
        treeNodes.forEach(item->{
            nodeMap.put(item.getId(), item);
            if (item.getParentid().equals(id)){
                nodes.add(item);
            }
            //找到该节点的父节点
            String parent_id = item.getParentid();
            CourseCategoryTreeDto parentNode = nodeMap.get(parent_id);
            if (parentNode != null){
                List<CourseCategory> childrenTreeNodes = parentNode.getChildrenTreeNodes();
                if (childrenTreeNodes == null){
                    parentNode.setChildrenTreeNodes(new ArrayList<>());
                }
                //找到子节点,放到它的父节点的childrenTreeNodes属性中
                parentNode.getChildrenTreeNodes().add(item);
            }
        });
        return nodes;
    }
}
