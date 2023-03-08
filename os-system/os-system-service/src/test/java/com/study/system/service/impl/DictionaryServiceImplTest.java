package com.study.system.service.impl;

import com.study.system.service.DictionaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DictionaryServiceImplTest {

    @Autowired
    private DictionaryService dictionaryService;

    @Test
    void queryAll() {
        System.out.println("x");
    }

    @Test
    void getByCode() {
    }
}