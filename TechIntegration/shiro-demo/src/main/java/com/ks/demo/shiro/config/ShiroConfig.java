package com.ks.demo.shiro.config;


import com.ks.demo.shiro.realm.ShiroRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;

/**
 * Apache Shiro配置类new一个SecurityManager，把自己的Realm注入进来
 */
@Configuration
public class ShiroConfig {
    /**
     * 注意：这个方法的返回值为SecurityManager时会报错：
     * The bean 'securityManager', defined in class path resource
     * [org/apache/shiro/spring/config/web/autoconfigure/ShiroWebAutoConfiguration.class],
     * could not be registered. A bean with that name has already been defined
     * in class path resource [net/hackyle/boot/config/ShiroConfig.class] and overriding is disabled.
     *
     * 解决方案：使用DefaultWebSecurityManager代替SecurityManager
     */
    @Bean
    public DefaultWebSecurityManager securityManager(){
        //配置SecurityManager，并注入shiroRealm
        //注意：SecurityManager在JDK的lang包下也有，需要导入org.apache.shiro.mgt包下的
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 登录的url
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 登录成功后跳转的url
        shiroFilterFactoryBean.setSuccessUrl("/index");
        // 未授权url
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        // 定义filterChain，静态资源不拦截
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/fonts/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        // druid数据源监控页面不拦截
        filterChainDefinitionMap.put("/druid/**", "anon");
        // 配置退出过滤器，其中具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/", "anon");
        // 除上以外所有url都必须认证通过才可以访问，未通过认证自动访问LoginUrl
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public ShiroRealm shiroRealm(){
        // 配置Realm，需自己实现
        return new ShiroRealm();
    }
}
