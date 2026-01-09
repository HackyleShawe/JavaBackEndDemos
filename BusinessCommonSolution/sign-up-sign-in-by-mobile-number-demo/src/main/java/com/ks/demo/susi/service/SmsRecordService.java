package com.ks.demo.susi.service;

import com.ks.demo.susi.entity.SmsRecordEntity;
import com.ks.demo.susi.mapper.SmsRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsRecordService {
    @Autowired
    private SmsRecordMapper smsRecordMapper;

    /**
     * 短信验证码的发送记录
     */
    public void record(SmsRecordEntity smsRecordEntity) {
        smsRecordMapper.insert(smsRecordEntity);
    }

    /**
     * 更新验证码的状态
     */
    public void updateStatus(SmsRecordEntity smsRecordEntity) {
        smsRecordMapper.updateStatus(smsRecordEntity);
    }
}
