**AJ-Captcha**

- 相比于传统验证码，用户只需要根据显示的验证产生指定的行为轨迹，不需要通过键盘输入，极大地提升了用户体验。
- 两种实现方式：滑动拼图 blockPuzzle  文字点选 clickWord



Doc：https://ajcaptcha.beliefteam.cn/captcha-doc/

Code：https://gitee.com/anji-plus/captcha    https://github.com/anji-plus/captcha



```xml
<!-- 整合SpringBoot -->
<dependency>
    <groupId>com.anji-plus</groupId>
    <artifactId>spring-boot-starter-captcha</artifactId>
    <version>1.3.0</version>
</dependency>

<!-- 普通整合 -->
<dependency>
    <groupId>com.anji-plus</groupId>
    <artifactId>captcha</artifactId>
    <version>1.3.0</version>
</dependency>
```



**使用步骤**

1. 导入POM依赖

2. 在application.properties中自**定义AJ-Captcha**：验证码类型、缓存、字体等

3. 下载底图于resources目录：https://github.com/anji-plus/captcha/tree/master/images

4. 后端整合SpringBoot教程、验证码API参数解释

   a)   https://ajcaptcha.beliefteam.cn/captcha-doc/captchaDoc/java.html#springboot

   b)   实例：com/ks/demo/ac/controller/AjCaptchaController.java

5. 前端：

   a)   整合HTML：https://ajcaptcha.beliefteam.cn/captcha-doc/captchaDoc/html.html

   b)   整合Vue：https://ajcaptcha.beliefteam.cn/captcha-doc/captchaDoc/vue.html

   c)   整合HTML实例：

​         i.      滑动验证：src/main/resources/static/slide-captcha.html

​         ii.      点击验证：src/main/resources/static/point-captcha.html

6. 测试

   a)   http://localhost:9696/slide-captcha.html

   b)   http://localhost:9696/point-captcha.html



 

**整合Redis**

1)   背景：现在的验证码数据保存在本机Session，想要将验证码数据保存在Redis该如何实现

2)   自定义一个类CaptchaCacheServiceRedisImpl，继承com.anji.captcha.service.CaptchaCacheService，重写其中核心方法。参考：https://github.com/anji-plus/captcha/blob/master/service/springboot/src/main/java/com/anji/captcha/demo/service/CaptchaCacheServiceRedisImpl.java

3)   在application.properties中的"aj.captcha.cache-type"值为"redis"

4)   在resources目录下创建目录META-INF/service

5)   再创建文件：com.anji.captcha.service.CaptchaCacheService，指定自定义缓存的实现类：com.ks.demo.ac.captcha.CaptchaCacheServiceRedisImpl



