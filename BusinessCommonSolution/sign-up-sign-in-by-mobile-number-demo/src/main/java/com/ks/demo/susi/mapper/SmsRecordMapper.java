package com.ks.demo.susi.mapper;

import com.ks.demo.susi.entity.SmsRecordEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsRecordMapper {

    void insert(SmsRecordEntity smsRecordEntity);

    void updateStatus(SmsRecordEntity smsRecordEntity);
}




