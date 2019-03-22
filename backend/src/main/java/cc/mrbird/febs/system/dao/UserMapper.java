package cc.mrbird.febs.system.dao;

import cc.mrbird.febs.common.config.MyMapper;
import cc.mrbird.febs.system.domain.User;

import java.util.List;

public interface UserMapper extends MyMapper<User> {

	List<User> findUserDetail(User user);
}