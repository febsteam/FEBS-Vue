package cc.mrbird.febs.system.service.impl;

import cc.mrbird.febs.common.service.impl.BaseService;
import cc.mrbird.febs.system.dao.TestMapper;
import cc.mrbird.febs.system.domain.Test;
import cc.mrbird.febs.system.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("testService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TestServiceImpl extends BaseService<Test> implements TestService {

    @Value("${febs.max.batch.insert.num}")
    private int batchInsertMaxNum;

    @Autowired
    private TestMapper testMapper;

    @Override
    public List<Test> findTests() {
        try{
            Example example = new Example(Test.class);
            example.setOrderByClause("create_time desc");
            return this.selectByExample(example);
        }catch (Exception e){
            log.error("获取信息失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void batchInsert(List<Test> list) {
        int total = list.size();
        int max = batchInsertMaxNum;
        int count = total / max;
        int left = total % max;
        int length;
        if (left == 0) length = count;
        else length = count + 1;
        for (int i = 0; i < length; i++) {
            int start = max * i;
            int end = max * (i + 1);
            if (i != count) {
                log.info("正在插入第" + (start + 1) + " ~ " + end + "条记录 ······");
                this.testMapper.insertList(list.subList(start, end));
            } else {
                end = total;
                log.info("正在插入第" + (start + 1) + " ~ " + end + "条记录 ······");
                this.testMapper.insertList(list.subList(start, end));
            }
        }
    }
}
