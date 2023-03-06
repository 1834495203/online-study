package com.study.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.system.mapper.DictionaryMapper;
import com.study.system.model.po.Dictionary;
import com.study.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author GLaDOS
 */
@Slf4j
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Override
    public List<Dictionary> queryAll() {

        List<Dictionary> list = this.list();
        return list;
    }

    @Override
    public Dictionary getByCode(String code) {

        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getCode, code);

        Dictionary dictionary = this.getOne(queryWrapper);

        return dictionary;
    }
}
