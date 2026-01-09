package com.ks.redisdemo.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis") //从配置文件里读取redis开头的配置项
public class RedisProperties {
    private String host = "localhost";
    private int port = 6379;

    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
}
