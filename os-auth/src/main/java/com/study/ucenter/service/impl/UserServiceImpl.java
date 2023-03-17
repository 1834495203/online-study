package com.study.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.ucenter.mapper.XcUserMapper;
import com.study.ucenter.model.dto.AuthParamsDto;
import com.study.ucenter.model.po.XcUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
        //将传入的字符串转换为AuthParamDto对象
        AuthParamsDto authParamsDto = null;

        try{
            authParamsDto = JSON.parseObject(user, AuthParamsDto.class);
        }catch (Exception e){
            throw new RuntimeException("请求的参数不符合要求");
        }

        String username = authParamsDto.getUsername();

        // 根据username查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (xcUser == null){
            // 查询到用户不存在, 要返回null即可
            return null;
        }

        //如果查到用户拿到正确的密码, 最终封装成了UserDetails对象给spring security框架返回, 由框架进行比对
        String password = xcUser.getPassword();
        xcUser.setPassword(null);
        String u = JSON.toJSONString(xcUser);
        String[] authorities = {"test"};
        return User.withUsername(u).password(password).authorities(authorities).build();
    }
}
