package com.ks.demo.datatime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 增强型JSON工具类
 * 封装Jackson核心配置，提供通用的序列化/反序列化方法
 */
public class JacksonUtils {
    // 全局单例ObjectMapper（线程安全，无需重复创建）
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 静态代码块初始化配置（项目启动时执行一次）
    static {
        // 1. 注册JDK8+时间模块（解决LocalDateTime等序列化问题）
        MAPPER.registerModule(new JavaTimeModule());
        // 2. 禁用日期序列化为时间戳（改为字符串格式）
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 3. 启用格式化输出（JSON带缩进，便于调试）
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        // 4. 过滤NULL值字段（序列化时不输出空值）
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 5. 忽略未知字段（反序列化时，JSON有多余字段不报错）
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 6. 空Bean序列化不报错（避免无字段的类序列化抛出异常）
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 可选：设置字段命名策略（比如下划线命名）
        // MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    /**
     * 对象序列化为JSON字符串
     *
     * @paramobj 待序列化的对象（任意类型）
     * @return格式化后的JSON字符串
     * @throwsRuntimeException 序列化失败时抛出运行时异常
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    /**
     * JSON字符串反序列化为指定类型对象
     *
     * @paramjson JSON字符串
     * @paramclazz 目标对象类型
     * @param<T> 泛型，适配任意实体类
     * @return反序列化后的对象
     * @throwsRuntimeException 反序列化失败时抛出运行时异常
     */
    public static<T> T parseJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

}
