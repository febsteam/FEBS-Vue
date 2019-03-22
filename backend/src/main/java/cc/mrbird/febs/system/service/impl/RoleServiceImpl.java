package cc.mrbird.febs.system.service.impl;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.service.impl.BaseService;
import cc.mrbird.febs.common.utils.FebsUtil;
import cc.mrbird.febs.system.dao.RoleMapper;
import cc.mrbird.febs.system.dao.RoleMenuMapper;
import cc.mrbird.febs.system.domain.Role;
import cc.mrbird.febs.system.domain.RoleMenu;
import cc.mrbird.febs.system.manager.UserManager;
import cc.mrbird.febs.system.service.RoleMenuServie;
import cc.mrbird.febs.system.service.RoleService;
import cc.mrbird.febs.system.service.UserRoleService;
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
@Service("roleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RoleServiceImpl extends BaseService<Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleMenuServie roleMenuService;
    @Autowired
    private UserManager userManager;

    @Override
    public List<Role> findRoles(Role role, QueryRequest request) {
        try {
            Example example = new Example(Role.class);
            Example.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(role.getRoleName())) {
                criteria.andCondition("role_name=", role.getRoleName());
            }
            if (StringUtils.isNotBlank(role.getCreateTimeFrom()) && StringUtils.isNotBlank(role.getCreateTimeTo())) {
                criteria.andCondition("date_format(CREATE_TIME,'%Y-%m-%d') >=", role.getCreateTimeFrom());
                criteria.andCondition("date_format(CREATE_TIME,'%Y-%m-%d') <=", role.getCreateTimeTo());
            }
            FebsUtil.handleSort(request, example, "role_id");
            return this.selectByExample(example);
        } catch (Exception e) {
            log.error("获取角色信息失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Role> findUserRole(String userName) {
        return this.roleMapper.findUserRole(userName);
    }

    @Override
    public Role findByName(String roleName) {
        Example example = new Example(Role.class);
        example.createCriteria().andCondition("lower(role_name)=", roleName.toLowerCase());
        List<Role> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void createRole(Role role) {
        role.setCreateTime(new Date());
        this.save(role);

        String[] menuIds = role.getMenuId().split(",");
        setRoleMenus(role, menuIds);
    }

    @Override
    public void deleteRoles(String[] roleIds) throws Exception {
        // 查找这些角色关联了那些用户
        List<String> userIds = this.userRoleService.findUserIdsByRoleId(roleIds);

        List<String> list = Arrays.asList(roleIds);
        this.batchDelete(list, "roleId", Role.class);

        this.roleMenuService.deleteRoleMenusByRoleId(roleIds);
        this.userRoleService.deleteUserRolesByRoleId(roleIds);

        // 重新将这些用户的角色和权限缓存到 Redis中
        this.userManager.loadUserPermissionRoleRedisCache(userIds);

    }

    @Override
    public void updateRole(Role role) throws Exception {
        // 查找这些角色关联了那些用户
        String[] roleId = {String.valueOf(role.getRoleId())};
        List<String> userIds = this.userRoleService.findUserIdsByRoleId(roleId);

        role.setModifyTime(new Date());
        this.updateNotNull(role);

        Example example = new Example(RoleMenu.class);
        example.createCriteria().andCondition("role_id=", role.getRoleId());
        this.roleMenuMapper.deleteByExample(example);

        String[] menuIds = role.getMenuId().split(",");
        setRoleMenus(role, menuIds);

        // 重新将这些用户的角色和权限缓存到 Redis中
        this.userManager.loadUserPermissionRoleRedisCache(userIds);
    }

    private void setRoleMenus(Role role, String[] menuIds) {
        Arrays.stream(menuIds).forEach(menuId -> {
            RoleMenu rm = new RoleMenu();
            rm.setMenuId(Long.valueOf(menuId));
            rm.setRoleId(role.getRoleId());
            this.roleMenuMapper.insert(rm);
        });
    }
}
