package cc.mrbird.febs.system.service;

import cc.mrbird.febs.common.service.IService;
import cc.mrbird.febs.system.domain.Test;

import java.util.List;

public interface TestService extends IService<Test> {

    List<Test> findTests();

    /**
     * 批量插入
     * @param list List<Test>
     */
    void batchInsert(List<Test> list);
}
