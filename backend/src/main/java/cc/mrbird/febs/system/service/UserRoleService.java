package cc.mrbird.febs.system.service;


import cc.mrbird.febs.common.service.IService;
import cc.mrbird.febs.system.domain.UserRole;

import java.util.List;

public interface UserRoleService extends IService<UserRole> {

	void deleteUserRolesByRoleId(String[] roleIds);

	void deleteUserRolesByUserId(String[] userIds);

	List<String> findUserIdsByRoleId(String[] roleIds);
}
