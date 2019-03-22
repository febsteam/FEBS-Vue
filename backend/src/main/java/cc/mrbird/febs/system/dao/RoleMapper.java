package cc.mrbird.febs.system.dao;

import cc.mrbird.febs.common.config.MyMapper;
import cc.mrbird.febs.system.domain.Role;

import java.util.List;

public interface RoleMapper extends MyMapper<Role> {
	
	List<Role> findUserRole(String userName);
	
}