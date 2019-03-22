package cc.mrbird.febs.job.service.impl;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.service.impl.BaseService;
import cc.mrbird.febs.common.utils.FebsUtil;
import cc.mrbird.febs.job.domain.JobLog;
import cc.mrbird.febs.job.service.JobLogService;
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
@Service("JobLogService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class JobLogServiceImpl extends BaseService<JobLog> implements JobLogService {

    @Override
    public List<JobLog> findJobLogs(QueryRequest request, JobLog jobLog) {
        try {
            Example example = new Example(JobLog.class);
            Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(jobLog.getBeanName())) {
                criteria.andCondition("bean_name=", jobLog.getBeanName());
            }
            if (StringUtils.isNotBlank(jobLog.getMethodName())) {
                criteria.andCondition("method_name=", jobLog.getMethodName());
            }
            if (StringUtils.isNotBlank(jobLog.getParams())) {
                criteria.andCondition("params like", "%" + jobLog.getParams() + "%");
            }
            if (StringUtils.isNotBlank(jobLog.getStatus())) {
                criteria.andCondition("status=", Long.valueOf(jobLog.getStatus()));
            }
            if (StringUtils.isNotBlank(jobLog.getCreateTimeFrom()) && StringUtils.isNotBlank(jobLog.getCreateTimeTo())) {
                criteria.andCondition("date_format(CREATE_TIME,'%Y-%m-%d') >=", jobLog.getCreateTimeFrom());
                criteria.andCondition("date_format(CREATE_TIME,'%Y-%m-%d') <=", jobLog.getCreateTimeTo());
            }

            FebsUtil.handleSort(request, example, "create_time desc");
            return this.selectByExample(example);
        } catch (Exception e) {
            log.error("获取调度日志信息失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void saveJobLog(JobLog log) {
        this.save(log);
    }

    @Override
    @Transactional
    public void deleteJobLogs(String[] jobLogIds) {
        List<String> list = Arrays.asList(jobLogIds);
        this.batchDelete(list, "logId", JobLog.class);
    }

}
