**Java Web开发中（Java EE、Spring生态）常用的业务开发解决方案、组件整合、技术使用示例、项目骨架。**

----

**仓库的目标：**

- 秉承着"简单"和"容易"这两项原则，将Java Web开发中常用的组件，**拆分**为一个个可直接**独立运行**的小项目
- 对于每一个小项目，都能够运行起来，可以直接**看到效果**
- 仓库中的各个模块代码，可**直接复用**

**重要说明**

- 每个模块的详细说明、技术细节，在我的个人技术博客中：https://blog.hackyle.com
- 本仓库只存储完整的代码
- 每个模块的Readme文件中有最重要的关键说明

**文件夹释义**

- 技术整合（TechIntegration）
  - 将某项技术整合Java或Spring生态
  - 例如，‘springcache-redis’就是将SpringCache API与Redis的整合

- 技术解决方案（TechSolution）
  - 针对某一技术需求，通过引用某中技术来满足
  - 例如：在技术上如何设计登录认证和授权鉴权，使用SpringSecurity

- 业务通用解决方案（BusinessCommonSolution）
  - 某些通用的业务场景，提出一些解决方案，并实现
  - 某些支撑业务的基础设施服务
  - 例如：如何设计与实现一个通用场景的用户中心
  - 例如：‘multi-options-storage-query-demo’就是针对多选项的存储与查询的业务场景，怎么快速查询的解决方案

- 业务领域解决方案（BusinessDomainSolution）
  - 针对特有领域的业务场景，设计一些算法、技术来实现业务需求
  - 例如：在电商领域，如何实现秒杀、下单优惠券功能


**意义**

- 通过几年的工作，我发现很多时候我们都是**在不同环境下写重复的代码、做重复的事** ，如果能够复用以前代码、经验，能够快速、高效地解决问题
- 所以，我建议每一位开发者，都要**建立属于自己的代码片段与模板** ，后续可以直接复用，从而避免每次都从0开始构建
- 代码片段或模板的**粒度要做到尽可能地小、依赖要尽可能地少**
- 在浏览我的代码片段仓库时，建议使用一种快速阅览插件，我使用的是"Octotree - GitHub code tree"


# TechIntegration


**springcache-redis**

- SpringCache整合Redis示例
- 它们之间的关系：SpringCache是Spring对缓存的一种规范。Redis才是真正进行缓存的具体工具。可以类比： JDBC规范与实现该规范的MySQL驱动（com.mysql.cj.jdbc.Driver）一样
- 完整项目：[springcache-redis](./TechIntegration/springcache-redis)
- 详细博文：https://blog.hackyle.com/article/java-demo/springcache-redis

**frontend-maven-package-plugin-demo**

- 前后端统一打包Maven插件
- 完整项目：[frontend-maven-package-plugin-demo](./TechIntegration/frontend-maven-package-plugin-demo)
- ReadMe：[README.md](./TechIntegration/frontend-maven-package-plugin-demo/README.md)


**spring-starter-demo**

- 第一个starter实例
- 自定义与实现一个redis-starter，实现自动装配Jedis
- 完整项目：[spring-starter-demo](./TechIntegration/spring-starter-demo)
- ReadMe：[README.md](./TechIntegration/spring-starter-demo/README.md)

**shiro-demo**

- ApacheShiro整合SpringBoot示例
- 自定义与实现一个redis-starter，实现自动装配Jedis
- 完整项目：[shiro-demo](./TechIntegration/shiro-demo)
- ReadMe：[README.md](./TechIntegration/shiro-demo/README.md)

**valid-validated-demo**

- valid与validated的使用示例
- valid是一种Java规范，Hibernate-validator对其进行了实现；validated是Spring的一种校验机制
- 完整项目：[valid-validated-demo](./TechIntegration/valid-validated-demo)
- 详细博文：https://blog.hackyle.com/article/java-demo/valid-validated-demo


**spring-springmvc-mybatis**

- Spring+SpringMVC+MyBatis整合示例
- 完整项目：[spring-springmvc-mybatis](./TechIntegration/spring-springmvc-mybatis)
- ReadMe：[README.md](./TechIntegration/spring-springmvc-mybatis/README.md)

# TechSolution


**QR-code-zxing-demo**

- 二维码的生成与解析开源工具zxing的使用示例
- 完整项目：[QR-code-zxing-demo](./TechSolution/QR-code-zxing-demo)

**minio-demo**

- MinIO是一款高性能的分布式对象存储服务解决方案，常作为Web服务的文件存储服务器，提供文件的上传和下载功能。
- MinIO整合SpringBoot示例
- 完整项目：[minio-demo](./TechSolution/minio-demo)
- 详细博文：https://blog.hackyle.com/article/java-demo/minio-demo


## 验证码

**kaptcha-demo**

- Kaptcha 是一个Google开源、可自由配置的图片验证码生成工具
- Kaptcha在SpringBoot环境下的用法实例
- 功能特性：支持**英文、数字**的验证码
- 完整项目：[kaptcha-demo](./TechSolution/kaptcha-demo)
- 详细博文：https://blog.hackyle.com/article/java-demo/kaptcha

**easy-captcha-demo**

- easy-captcha整合SpringBoot环境的用法实例
- 功能特性：支持**英文数字、算术、中文字符、闪图**的验证码
- 完整项目：[easy-captcha-demo](./TechSolution/easy-captcha-demo)
- ReadMe：[README.md](./TechSolution/easy-captcha-demo/README.md)

**aj-captcha-demo**

- aj-captcha整合SpringBoot环境的用法实例
- 功能特性：支持**滑动拼图、文字点选**的验证码
- 完整项目：[aj-captcha-demo](./TechSolution/aj-captcha-demo)
- ReadMe：[README.md](./TechSolution/aj-captcha-demo/README.md)



# BusinessCommonSolution

**multi-options-storage-query-demo**

- 一种多选项的存储与高效查询的解决方案
- 完整项目：[multi-options-storage-query-demo](./BusinessCommonSolution/multi-options-storage-query-demo)
- ReadMe：[README.md](./BusinessCommonSolution/multi-options-storage-query-demo/README.md)

**sign-up-sign-in-by-mobile-number-demo**

- 通过手机号实现注册登录

- 完整项目：[sign-up-sign-in-by-mobile-number-demo](./BusinessCommonSolution/sign-up-sign-in-by-mobile-number-demo)
- ReadMe：[README.md](./BusinessCommonSolution/sign-up-sign-in-by-mobile-number-demo/README.md)

**workday-holiday-demo**

- 工作日和节假日的判定和计算
- 完整项目：[workday-holiday-demo](./BusinessCommonSolution/workday-holiday-demo)
- ReadMe：[README.md](./BusinessCommonSolution/workday-holiday-demo/README.md)



# BusinessDomainSolution






