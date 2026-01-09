package com.ks.demo.susi.mapper;

import com.ks.demo.susi.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int existsByMobilePhone(@Param("mobilePhone")String mobilePhone);

    int insert(UserEntity userEntity);
}




