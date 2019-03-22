package cc.mrbird.febs.system.service;

import cc.mrbird.febs.common.service.IService;
import cc.mrbird.febs.system.domain.RoleMenu;

import java.util.List;

public interface RoleMenuServie extends IService<RoleMenu> {

    void deleteRoleMenusByRoleId(String[] roleIds);

    void deleteRoleMenusByMenuId(String[] menuIds);

    List<RoleMenu> getRoleMenusByRoleId(String roleId);
}
