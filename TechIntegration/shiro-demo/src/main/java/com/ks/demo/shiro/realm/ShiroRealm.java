package com.ks.demo.shiro.realm;

import com.ks.demo.shiro.dao.PermissionDao;
import com.ks.demo.shiro.dao.RoleDao;
import com.ks.demo.shiro.dao.UserDao;
import com.ks.demo.shiro.entity.PermissionEntity;
import com.ks.demo.shiro.entity.RoleEntity;
import com.ks.demo.shiro.entity.UserEntity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 验证（认证）、授权的业务逻辑
 * 需要从Dao层获取数据，例如在用户登录验证时需要到数据库中获取该用户的密码
 */
public class ShiroRealm extends AuthorizingRealm {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    /**
     * authentication发音[ɔ:ˌθentɪ’keɪʃn]，认证，身份验证的意思，即登录时验证用户的合法性，比如验证用户名和密码
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());

        if(username == null || "".equals(username.trim()) || password == null || "".equals(password.trim())) {
            throw new IllegalArgumentException("入参不合法");
        }

        //查询数据库，检查是否有该条记录
        UserEntity user = userDao.loginCheck(username, password);
        if(user == null) {
            throw new RuntimeException("用户名或密码不正确，登录认证失败！");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());
        return info;
    }

    /**
     * authorization发音[ˌɔ:θəraɪˈzeɪʃn]，为授权，批准的意思，即授予用户的角色和权限等信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        UserEntity user = (UserEntity) SecurityUtils.getSubject().getPrincipal();
        if(user == null || user.getUsername() == null || "".equals(user.getUsername().trim())) {
            throw new IllegalArgumentException("入参不合法");
        }

        try {
            // 获取用户角色集
            List<RoleEntity> roleEntities = roleDao.queryRoleByUserId(user.getId());
            Set<String> roleNameSet = roleEntities.stream().map(RoleEntity::getName).collect(Collectors.toSet());
            simpleAuthorizationInfo.setRoles(roleNameSet);

            // 获取用户权限集
            List<PermissionEntity> permissionEntities = permissionDao.queryPermissionByUserId(user.getId());
            Set<String> permissionNameSet = permissionEntities.stream().map(PermissionEntity::getName).collect(Collectors.toSet());
            simpleAuthorizationInfo.setStringPermissions(permissionNameSet);
        } catch (Exception e) {
            //LOGGER.error("获取授权数据出现异常：", e);
            e.printStackTrace();
            throw new AuthorizationException("获取授权数据出现异常");
        }

        return simpleAuthorizationInfo;
    }
}
