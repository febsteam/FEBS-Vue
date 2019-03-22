package cc.mrbird.febs.system.dao;

import cc.mrbird.febs.common.config.MyMapper;
import cc.mrbird.febs.system.domain.Dept;

import java.util.List;

public interface DeptMapper extends MyMapper<Dept> {

	/**
	 * 递归删除部门
	 *
	 * @param deptId deptId
	 */
	void deleteDepts(String deptId);
}