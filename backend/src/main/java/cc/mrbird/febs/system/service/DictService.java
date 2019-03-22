package cc.mrbird.febs.system.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.service.IService;
import cc.mrbird.febs.system.domain.Dict;

import java.util.List;

public interface DictService extends IService<Dict> {

    List<Dict> findDicts(QueryRequest request, Dict dict);

    void createDict(Dict dict);

    void updateDict(Dict dicdt);

    void deleteDicts(String[] dictIds);

}
