package cc.mrbird.febs.system.service.impl;

import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.service.CacheService;
import cc.mrbird.febs.common.service.impl.BaseService;
import cc.mrbird.febs.common.utils.MD5Util;
import cc.mrbird.febs.system.dao.UserMapper;
import cc.mrbird.febs.system.dao.UserRoleMapper;
import cc.mrbird.febs.system.domain.User;
import cc.mrbird.febs.system.domain.UserRole;
import cc.mrbird.febs.system.manager.UserManager;
import cc.mrbird.febs.system.service.UserConfigService;
import cc.mrbird.febs.system.service.UserRoleService;
import cc.mrbird.febs.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("userService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl extends BaseService<User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserConfigService userConfigService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserManager userManager;


    @Override
    public User findByName(String username) {
        User param = new User();
        param.setUsername(username);
        List<User> list = findUserDetail(param, new QueryRequest());
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public User findById(String userId) {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("user_id=", Long.valueOf(userId));
        List<User> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<User> findUserDetail(User user, QueryRequest request) {
        try {
            if (request.getSortField() != null) {
                user.setSortField(request.getSortField());
                if (StringUtils.equals(FebsConstant.ORDER_ASC, request.getSortOrder()))
                    user.setSortOrder("asc");
                else if (StringUtils.equals(FebsConstant.ORDER_DESC, request.getSortOrder()))
                    user.setSortOrder("desc");
            }
            return this.userMapper.findUserDetail(user);
        } catch (Exception e) {
            log.error("查询用户异常", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void updateLoginTime(String username) throws Exception {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("username=", username);
        User user = new User();
        user.setLastLoginTime(new Date());
        this.userMapper.updateByExampleSelective(user, example);

        // 重新将用户信息加载到 redis中
        cacheService.saveUser(username);
    }

    @Override
    @Transactional
    public void createUser(User user) throws Exception {
        // 创建用户
        user.setCreateTime(new Date());
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setPassword(MD5Util.encrypt(user.getUsername(), User.DEFAULT_PASSWORD));
        this.save(user);

        // 保存用户角色
        String[] roles = user.getRoleId().split(",");
        setUserRoles(user, roles);

        // 创建用户默认的个性化配置
        userConfigService.initDefaultUserConfig(String.valueOf(user.getUserId()));

        // 将用户相关信息保存到 Redis中
        userManager.loadUserRedisCache(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) throws Exception {
        // 更新用户
        user.setPassword(null);
        user.setModifyTime(new Date());
        this.updateNotNull(user);

        Example example = new Example(UserRole.class);
        example.createCriteria().andCondition("user_id=", user.getUserId());
        this.userRoleMapper.deleteByExample(example);

        String[] roles = user.getRoleId().split(",");
        setUserRoles(user, roles);

        // 重新将用户信息，用户角色信息，用户权限信息 加载到 redis中
        cacheService.saveUser(user.getUsername());
        cacheService.saveRoles(user.getUsername());
        cacheService.savePermissions(user.getUsername());
    }

    @Override
    @Transactional
    public void deleteUsers(String[] userIds) throws Exception {
        // 先删除相应的缓存
        this.userManager.deleteUserRedisCache(userIds);

        List<String> list = Arrays.asList(userIds);
        this.batchDelete(list, "userId", User.class);

        // 删除用户角色
        this.userRoleService.deleteUserRolesByUserId(userIds);
        // 删除用户个性化配置
        this.userConfigService.deleteByUserId(userIds);
    }

    @Override
    @Transactional
    public void updateProfile(User user) throws Exception {
        this.updateNotNull(user);
        // 重新缓存用户信息
        cacheService.saveUser(user.getUsername());
    }

    @Override
    @Transactional
    public void updateAvatar(String username, String avatar) throws Exception {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("username=", username);
        User user = new User();
        user.setAvatar(avatar);
        this.userMapper.updateByExampleSelective(user, example);
        // 重新缓存用户信息
        cacheService.saveUser(username);
    }

    @Override
    @Transactional
    public void updatePassword(String username, String password) throws Exception {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("username=", username);
        User user = new User();
        user.setPassword(MD5Util.encrypt(username, password));
        this.userMapper.updateByExampleSelective(user, example);
        // 重新缓存用户信息
        cacheService.saveUser(username);
    }

    @Override
    @Transactional
    public void regist(String username, String password) throws Exception {
        User user = new User();
        user.setPassword(MD5Util.encrypt(username, password));
        user.setUsername(username);
        user.setCreateTime(new Date());
        user.setStatus(User.STATUS_VALID);
        user.setSsex(User.SEX_UNKNOW);
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setDescription("注册用户");
        this.save(user);

        UserRole ur = new UserRole();
        ur.setUserId(user.getUserId());
        ur.setRoleId(2L); // 注册用户角色 ID
        this.userRoleMapper.insert(ur);

        // 创建用户默认的个性化配置
        userConfigService.initDefaultUserConfig(String.valueOf(user.getUserId()));
        // 将用户相关信息保存到 Redis中
        userManager.loadUserRedisCache(user);

    }

    @Override
    @Transactional
    public void resetPassword(String[] usernames) throws Exception {
        for (String username: usernames) {

            Example example = new Example(User.class);
            example.createCriteria().andCondition("username=", username);
            User user = new User();
            user.setPassword(MD5Util.encrypt(username, User.DEFAULT_PASSWORD));
            this.userMapper.updateByExampleSelective(user, example);

            // 重新将用户信息加载到 redis中
            cacheService.saveUser(username);
        }

    }

    private void setUserRoles(User user, String[] roles) {
        Arrays.stream(roles).forEach(roleId -> {
            UserRole ur = new UserRole();
            ur.setUserId(user.getUserId());
            ur.setRoleId(Long.valueOf(roleId));
            this.userRoleMapper.insert(ur);
        });
    }
}
