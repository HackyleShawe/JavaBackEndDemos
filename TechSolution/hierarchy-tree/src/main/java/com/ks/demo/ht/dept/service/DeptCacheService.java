package com.ks.demo.ht.dept.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ks.demo.ht.dept.model.DeptVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存场景：
 * 查整棵树（组织架构）：key: sys:dept:tree；value: JSON（完整树结构）
 * 查子节点（注意不是子树，而是儿子节点）：key: sys:dept:children:{pid}；value: List<Dept>
 * 查某节点子树：key: sys:dept:subtree:{id}；value: List<Dept>（整个子树）
 * 查节点（热节点）：
 *  单个缓存方案：Key：sys:dept:{id}；value:JSON-Dept
 *  全量缓存方案：key: sys:dept:map；field: deptId；value: Dept JSON
 *
 */
@Slf4j
@Service
public class DeptCacheService {
    private static final String TREE_KEY = "sys:dept:tree";
    private static final String TREE_CHILDREN_PREFIX = "sys:dept:children:";
    private static final String TREE_SUBTREE_PREFIX = "sys:dept:subtree:";
    private static final String TREE_MAP_KEY = "sys:dept:map";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void putTree(List<DeptVo> deptVos) {
        //这种方式有什么问题？
        //delete + 写入：不是原子操作，导致读请求可能会拿到空数据
        //List 结构不适合这个场景
        //没有防击穿/空保护：如果 refresh 失败，整个 KEY 直接没了
        //redisTemplate.delete(KEY);
        //redisTemplate.opsForList().rightPushAll(KEY, deptVos);
        //redisTemplate.expire(KEY, 12, TimeUnit.HOURS);

        redisTemplate.opsForValue().set(TREE_KEY, JSON.toJSONString(deptVos), 12, TimeUnit.HOURS);
    }
    public String getTree() {
        Object object = redisTemplate.opsForValue().get(TREE_KEY);
        return object == null ? null : String.valueOf(object);
    }
    public void evictTree() {
        redisTemplate.delete(TREE_KEY);
    }

    public void putChildren(long pid, List<DeptVo> children) {
        redisTemplate.opsForValue().set(TREE_CHILDREN_PREFIX+ pid, JSON.toJSONString(children), 12, TimeUnit.HOURS);
    }
    public List<DeptVo> getChildren(long pid) {
        Object object = redisTemplate.opsForValue().get(TREE_CHILDREN_PREFIX+ pid);
        if (object == null) {
            return null;
        }
        return JSONArray.parseArray(String.valueOf(object), DeptVo.class);
    }
    public void evictChildren(long pid) {
        redisTemplate.delete(TREE_CHILDREN_PREFIX+ pid);
    }
    public void evictAllChildren() {
        Set<String> keys = redisTemplate.keys(TREE_CHILDREN_PREFIX + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void putSubTree(long pid, List<DeptVo> subTree) {
        redisTemplate.opsForValue().set(TREE_SUBTREE_PREFIX+ pid, JSON.toJSONString(subTree), 12, TimeUnit.HOURS);
    }
    public List<DeptVo> getSubTree(long pid) {
        Object object = redisTemplate.opsForValue().get(TREE_SUBTREE_PREFIX+ pid);
        if (object == null) {
            return null;
        }
        return JSONArray.parseArray(String.valueOf(object), DeptVo.class);
    }
    public void evictSubTree(long id) {
        redisTemplate.delete(TREE_SUBTREE_PREFIX+ id);
    }
    public void evictAllSubTree() {
        Set<String> keys = redisTemplate.keys(TREE_SUBTREE_PREFIX + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void put(List<DeptVo> deptVos) {
        if(CollectionUtil.isEmpty(deptVos)) {
            return;
        }
        Map<Long, DeptVo> deptVoMap = deptVos.stream().collect(Collectors.toMap(DeptVo::getId, deptVo -> deptVo));
        redisTemplate.opsForHash().putAll(TREE_MAP_KEY, deptVoMap);
        redisTemplate.expire(TREE_MAP_KEY,12,TimeUnit.HOURS);
    }
    public DeptVo get(long id) {
        Object object = redisTemplate.opsForHash().get(TREE_MAP_KEY, id);
        return object == null ? null : JSONObject.parseObject(JSON.toJSONString(object), DeptVo.class);
    }
    public void evict(long id) {
        redisTemplate.opsForHash().delete(TREE_MAP_KEY, id);
    }
}
