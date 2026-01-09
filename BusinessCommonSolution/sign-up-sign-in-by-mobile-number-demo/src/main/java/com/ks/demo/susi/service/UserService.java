package com.ks.demo.susi.service;

import com.ks.demo.susi.entity.UserEntity;
import com.ks.demo.susi.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 根据手机号检查是否已经注册
     */
    public boolean checkSignUp(String mobilePhone) {
        int exists = userMapper.existsByMobilePhone(mobilePhone);
        return exists >= 1;
    }

    /**
     * 注册
     */
    public boolean signUp(UserEntity userEntity) {
        int inserted = userMapper.insert(userEntity);
        return inserted == 1;
    }
}
