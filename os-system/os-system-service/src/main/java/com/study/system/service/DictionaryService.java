package com.study.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.system.model.po.Dictionary;

import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author GLaDOS
 */
public interface DictionaryService extends IService<Dictionary> {

    /**
     * 查询所有数据字典内容
     * @return 数据字典集合
     */
    List<Dictionary> queryAll();

    /**
     * 根据code查询数据字典
     * @param code -- String 数据字典Code
     * @return 数字字典
     */
    Dictionary getByCode(String code);
}
