package com.ks.demo.datatime.i18n;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;
import java.io.Serializable;

/**
 *
 * @TableName datetime_demo
 */
@TableName("datetime_demo")
@Data
public class DatetimeDemo implements Serializable {

    /**
     * 时间戳、UTC
     */
    private Instant timeStamp;
    /**
     * UTC标准时间的毫秒数，可解决Timestamp的2038问题
     */
    private Long epochMilli;

}
