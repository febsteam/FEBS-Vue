package cc.mrbird.febs.system.service;


import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.service.IService;
import cc.mrbird.febs.system.domain.Dept;

import java.util.List;
import java.util.Map;

public interface DeptService extends IService<Dept> {

    Map<String, Object> findDepts(QueryRequest request, Dept dept);

    List<Dept> findDepts(Dept dept, QueryRequest request);

    void createDept(Dept dept);

    void updateDept(Dept dept);

    void deleteDepts(String[] deptIds);
}
