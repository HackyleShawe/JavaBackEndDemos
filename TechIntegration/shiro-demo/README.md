 **权限的一般思想**

- 登录**认证**：根据用户名和密码进行登录

- **授权**：登录成功后对该用户名授予角色、权限（资源、数据）。例如：授予你对资源（“/resources/aa”）的增删改查权限。你就可以对该资源进行CRUD操作。否则，你是不能对该资源进行操作的。

- **鉴权**：用户访问某个资源时，检查该用户是否具备相应的角色、权限



# 权限RBAC

**RBAC**的两种方案：

- 基于角色的访问控制（Role-Based Access Control）

- 基于资源的访问控制（Resource-Based Access Control）

 **完成权限管理需要三个对象：**

1)   **用户**：主要包含用户名，密码和当前用户的角色信息，可实现认证操作。

2)   **角色**：主要包含角色名称，角色描述和当前角色拥有的权限信息，可实现授权操作。

3)   **权限**：权限是指是否可以访问某资源。



**实现SQL**：src/main/resources/sql/shiro_db.sql



# 认证授权逻辑

自定义Realm实现只需继承AuthorizingRealm类，然后实现doGetAuthorizationInfo()和doGetAuthenticationInfo()方法即可。

- authentication发音[ɔ:ˌθentɪ’keɪʃn]，认证，身份验证的意思，即登录时验证用户的合法性，比如验证用户名和密码。

- authorization发音[ˌɔ:θəraɪˈzeɪʃn]，为授权，批准的意思，即获取用户的角色和权限等信息；

**实现：**src/main/java/com/ks/demo/shiro/realm/**ShiroRealm**.java



# 认证Controller

**后端：**src/main/java/com/ks/demo/shiro/controller/LoginController.java

**前端：**src/main/resources/templates/login.html



# 鉴权Controller

**后端：**src/main/java/com/ks/demo/shiro/controller/ResourceController.java



# 自定义没有权限的页面

当前用户访问一个自己没有权限的资源地址，则会抛出如下异常：

>  org.apache.shiro.authz.AuthorizationException: Not authorized to invoke method:…



为了避免用户看到这种异常信息，在ShiroConfig中添加配置：

>  shiroFilterFactoryBean.setUnauthorizedUrl("/403");

没有权限的访问会自动重定向到/403，结果证明并不是这样。

这是因为，该设置只对filterChain起作用，比如在filterChain中设置了

> filterChainDefinitionMap.put("/person/resource", "perms[resource:update]");

如果用户没有user:update权限，那么当其访问/user/update的时候，页面会被重定向到/403。

**那么对于上面这个问题，我们可以定义一个全局异常捕获类，对于没有权限访问的资源，将会走我们自定义的异常Handler，并进行转发**

```java
package com.ks.demo.shiro.handler;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    /**
     * 捕获Shiro的没有权限（AuthorizationException）异常
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public String handleAuthorizationException() {
        return "403";
    }

    /**
     * 捕获Exception这个异常，跳转到error.html
     */
    @ExceptionHandler(value = Exception.class)
    public String handleException() {
        return "error";
    }
}
```

**实现：**src/main/java/com/ks/demo/shiro/handler/GlobalExceptionHandler.java

**403页面：**src/main/resources/templates/403.html

