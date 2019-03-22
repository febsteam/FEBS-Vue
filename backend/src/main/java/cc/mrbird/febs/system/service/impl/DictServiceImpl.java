package cc.mrbird.febs.system.service.impl;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.service.impl.BaseService;
import cc.mrbird.febs.system.domain.Dict;
import cc.mrbird.febs.system.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service("dictService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DictServiceImpl extends BaseService<Dict> implements DictService {

    @Override
    public List<Dict> findDicts(QueryRequest request, Dict dict) {
        try {
            Example example = new Example(Dict.class);
            Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(dict.getKeyy())) {
                criteria.andCondition("keyy=", Long.valueOf(dict.getKeyy()));
            }
            if (StringUtils.isNotBlank(dict.getValuee())) {
                criteria.andCondition("valuee=", dict.getValuee());
            }
            if (StringUtils.isNotBlank(dict.getTableName())) {
                criteria.andCondition("table_name=", dict.getTableName());
            }
            if (StringUtils.isNotBlank(dict.getFieldName())) {
                criteria.andCondition("field_name=", dict.getFieldName());
            }
            return this.selectByExample(example);
        } catch (Exception e) {
            log.error("获取字典信息失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void createDict(Dict dict) {
        this.save(dict);
    }

    @Override
    @Transactional
    public void updateDict(Dict dict) {
        this.updateNotNull(dict);
    }

    @Override
    @Transactional
    public void deleteDicts(String[] dictIds) {
        List<String> list = Arrays.asList(dictIds);
        this.batchDelete(list, "dictId", Dict.class);
    }
}
