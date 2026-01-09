package com.hackyle.cache.service;

import com.hackyle.cache.dto.UserinfoDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "cacheDemo")
@Service
public class CacheDemoService {

    /**
     * 先执行方法，再将方法的返回值放入缓存
     * #p0：表示取方法入参的第一个参数，并取属性为uid的值
     */
    //@CachePut(key = "'uid:'+#p0.uid") 等价于下一行代码
    @CachePut(key = "'uid:'+#userinfo.uid")
    public String saveUser(UserinfoDto userinfo) {
        //模拟保存、更新操作
        System.out.println("模拟保存、更新操作，先执行方法，再放入缓存中");

        return "保存、更新操作：cacheKey=uid:"+userinfo.getUid()+" ;userinfo="+userinfo;
    }

    //先执行方法，再删除缓存
    @CacheEvict(key = "'uid:' + #p0")
    public String deleteUser(long uid) {
        System.out.println("模拟删除操作，先执行方法，再删除缓存");

        return "删除操作：cacheKey=uid:" +uid +" ;uid="+uid;
    }

    /**
     * 先查询缓存，如果没有再执行方法
     * #p0：表示取方法入参的第一个参数
     */
    //@Cacheable(key = "'uid:'+#p0") 等价于下一行代码
    @Cacheable(key = "'uid:'+#uid")
    //@Cacheable(cacheNames = {"uid:"}, key = "'uid:'+#uid") cacheNames为本个缓存指定单独的cacheName，
    public String queryUserById(long uid) {
        //模拟查库
        UserinfoDto userinfo = new UserinfoDto();
        userinfo.setUid(uid);
        userinfo.setName("Kyle");
        userinfo.setGender("man");
        System.out.println("模拟查库操作，第二次请求如果走缓存，则不会执行到这句。");

        return "查询结果：cacheKey=uid:" +uid+ " ;uid="+uid+" ;userinfo="+userinfo;
    }
}
