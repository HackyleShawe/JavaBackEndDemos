**Spring+SpringMVC+MyBatis整合示例**

# 1.新建工程导入依赖

如pom.xml，依次导入Spring、SpringMVC、MyBatis的依赖，以及一些拓展依赖

```xml
<!--  Spring统一版本号  -->
<springVersion>5.2.6.RELEASE</springVersion>
```

# 2.Spring整合MyBatis

## 主配置文件

MyBatis主配置文件名：MyBatis-Config.xml

 **特性：**

- 由于我们使用的是代理 Dao接口 的模式，Dao接口的 具体实现类由 MyBatis 使用代理方式创建，所以此时 mybatis 配置文件不能删。

- 当我们整合 spring 和 mybatis 时，mybatis 创建的 Mapper.xml 文件名必须和 Dao接口 文件 名一致

- 与Spring整合后，因为像连接池之类的类全部交给Spring管理了，所以在MyBatis的配置文件内容将会很少，一般只会留下别名标签和设置标签

## Mapper接口和Mapper.xml

**在mapper包中的接口方法与XML文件中SQL标签的关联：**

1.   接口的**方法名**必须等于相应XML配置文件中的**id名**

2.   接口的方法**返回值**必须与XML文件中的返回**类型一致**

3.   接口方法的**入参类型**与XML中的入参类型一致

4.   XML中的**命名空间**必须绑定此接口

## 与Spring整合的XML

 注意，配置实体映射XML文件，只能在Spring-Mapper.xml或MyBatis主配置文件的其中之一配置。

 

### 在MyBatis主配置文件中导入MapperSQL

 **在MyBatis-Config.xml中导入MapperSQL共有三种方式：**

- package-name
- mapper-class
- mapper-resource



**MyBatis-Config.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<configuration>

    <!--与Spring整合后，因为像连接池之类的类全部交给Spring管理了，所以在MyBatis的配置文件内容将会很少-->
    <!--一般只会留下别名标签和设置标签，也可以将XML映射文件导入 -->

    <!--<settings>
        <setting name="" value=""/>
    </settings>-->

    <!-- 在MyBatis主配置文件中引入Mapper-XML -->
    <mappers>
        <!--方式一：强烈推荐使用这种方式, 因为不管注解还是配置XML映射文件，都是通用的-->
        <package name="net.hackyle.mapper"/>

        <!--方式二：需要一个个地指定具体的Mapper。不能使用*来代替所有的XML-->
        <mapper class="net.hackyle.mapper.PersonMapper"/>

        <!--方式三：指定为XML方式，不可使用注解-->
        <!--既可以XML映射文件放在和接口在一起，也可以放在resources同包的目录下-->
        <!--必须指定具体的Mapper，不能使用*来代替所有的XML-->
        <mapper resource="net/hackyle/mapper/PersonMapper.xml"/>
    </mappers>

    <!--注意路径的"."分割、"/"分割 -->
</configuration>

```

**Spring-Mapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!--
        Spring整合MyBatis专用配置文件
        Spring管理mapper层的配置文件，即将mapper层的所有类自动交给Spring管理
    -->

    <!--
        把连接池交给Spring管理
    -->
    <!--这里可以使用Spring-JDBC提供的连接池实现与数据库的连接，当然也可以使用其他连接池，如C3P0，DBCP，Druid -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <!--配置连接池属性-->
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/kdb?useSSL=true&amp;charsetEncoding=UTF-8"/>
        <property name="username" value="root"/>
        <property name="password" value="kyle"/>
    </bean>

    <!--
        把SqlSessionFactory交给Spring管理
    -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <!--绑定MyBatis配置文件，不可缺少-->
        <property name="configLocation" value="classpath:MyBatis-Config.xml"/>
    </bean>

    <!--配置mapper接口也可以注入到IOC容器中-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <property name="basePackage" value="net.hackyle.dao" />
    </bean>

</beans>
```

### 在Spring-Mapper.xml整合

在Spring-Mapper.xml中完成映射XML文件的导入：MapperScannerConfigurer-basePackage

**MyBatis-Config.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<configuration>

    <!--与Spring整合后，因为像连接池之类的类全部交给Spring管理了，所以在MyBatis的配置文件内容将会很少-->
    <!--一般只会留下别名标签和设置标签-->

</configuration>
```



**Spring-Mapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"

       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">

    <!--
        Spring整合MyBatis专用配置文件：Spring管理mapper层的配置文件，即将mapper层的所有类自动交给Spring管理
    -->

    <!--
        主要步骤：
        1.配置连接池（关联数据库配置文件）
        2.SqlSessionFactory
        3.配置实体映射XML文件
    -->

    <!--引入外部文件：引入外部的数据库配置文件-->
    <!--<context:property-placeholder location="classpath:database.properties"/>-->

    <!--
        配置连接池
    -->
    <!--这里可以使用Spring-JDBC提供的连接池实现与数据库的连接，当然也可以使用其他连接池，如C3P0，DBCP，Druid -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <!--配置连接池属性-->
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/kdb?useSSL=true&amp;charsetEncoding=UTF-8"/>
        <property name="username" value="root"/>
        <property name="password" value="kyle"/>
    </bean>

    <!--
        配置SqlSessionFactory
    -->
    <!--对于MyBatis来说，它就相当于原始JDBC中的连接池-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <!--绑定MyBatis配置文件-->
        <property name="configLocation" value="classpath:MyBatis-Config.xml"/>

        <!--只能将映射XML文件导入resources目录下的同包内-->
        <!--注意分割方式：斜杠、点-->
        <!--<property name="mapperLocations" value="classpath*:net/hackyle/mapper/*.xml"/>-->
    </bean>

    <!--自动扫描所有Mapper接口和文件，必须要有-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <property name="basePackage" value="net.hackyle.mapper" />
    </bean>

</beans>
```

# 3.Spring整合SpringMVC

 

## Spring-Controller.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"

       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       https://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/mvc
       https://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!--
         Spring管理controller层的配置文件，将controller层的所有类自动交给Spring管理
    -->

    <!--支持注解扫描-->
    <context:component-scan base-package="net.hackyle.controller" />

    <!--开启SpringMVC框架注解的支持。如果不开启，则无法访问RequestMapping指定的接口-->
    <mvc:annotation-driven/>


    <!--视图解析器-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/jsp/" />
        <property name="suffix" value=".jsp"/>
    </bean>


    <!-- 过滤静态资源 -->
    <!-- 注意：SpringMVC中的全部匹配是"/**", JavaEE中的全部匹配是"/*" -->
    <mvc:resources location="/css" mapping="/css/**"/>
    <mvc:resources location="/img/" mapping="/img/**"/>
    <!--<mvc:resources location="/js/" mapping="/js/**"/>-->
    <mvc:resources location="/plugins/" mapping="/plugins/**" />
    <!--开启SpringMVC注解的支持-->
    <mvc:annotation-driven/>

</beans>
```

## web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
         version="5.0">
    
    <welcome-file-list>
        <welcome-file>/index.jsp</welcome-file>
    </welcome-file-list>

    <!--全局乱码解决-->
    <filter>
        <filter-name>encodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>utf-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <!--注意：这里要使用"/*"，因为我们想要所有的文件都是以utf-8编码的-->
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <!--前端控制分发器-->
    <servlet>
        <servlet-name>dispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:ApplicationContext.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>dispatcherServlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

</web-app>
```

# 4.Spring主配置文件

**编写Spring的主配置文件：**ApplicationContext.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--Spring主配置文件-->

    <!--整合mapper层，导入MyBatis和Spring整合的配置文件-->
    <import resource="Spring-Mapper.xml"/>

    <!--整合service层-->
    <import resource="Spring-Service.xml"/>

    <!--整合controller层，导入SpringMVC的配置文件-->
    <import resource="Spring-Controller.xml"/>

</beans>
```

# 5.整合测试

 准备JSP页面

 完成对一个实体的CRUD

