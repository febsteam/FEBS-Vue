package cc.mrbird.febs.job.service.impl;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.service.impl.BaseService;
import cc.mrbird.febs.common.utils.FebsUtil;
import cc.mrbird.febs.job.dao.JobMapper;
import cc.mrbird.febs.job.domain.Job;
import cc.mrbird.febs.job.service.JobService;
import cc.mrbird.febs.job.util.ScheduleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("JobService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class JobServiceImpl extends BaseService<Job> implements JobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobMapper jobMapper;

    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() {
        List<Job> scheduleJobList = this.jobMapper.queryList();
        // 如果不存在，则创建
        scheduleJobList.forEach(scheduleJob -> {
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobId());
            if (cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        });
    }

    @Override
    public Job findJob(Long jobId) {
        return this.selectByKey(jobId);
    }

    @Override
    public List<Job> findJobs(QueryRequest request, Job job) {
        try {
            Example example = new Example(Job.class);
            Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(job.getBeanName())) {
                criteria.andCondition("bean_name=", job.getBeanName());
            }
            if (StringUtils.isNotBlank(job.getMethodName())) {
                criteria.andCondition("method_name=", job.getMethodName());
            }
            if (StringUtils.isNotBlank(job.getParams())) {
                criteria.andCondition("params like", "%" + job.getParams() + "%");
            }
            if (StringUtils.isNotBlank(job.getRemark())) {
                criteria.andCondition("remark like", "%" + job.getRemark() + "%");
            }
            if (StringUtils.isNotBlank(job.getStatus())) {
                criteria.andCondition("status=", Long.valueOf(job.getStatus()));
            }
            if (StringUtils.isNotBlank(job.getCreateTimeFrom()) && StringUtils.isNotBlank(job.getCreateTimeTo())) {
                criteria.andCondition("date_format(CREATE_TIME,'%Y-%m-%d') >=", job.getCreateTimeFrom());
                criteria.andCondition("date_format(CREATE_TIME,'%Y-%m-%d') <=", job.getCreateTimeTo());
            }

            FebsUtil.handleSort(request, example, "create_time");
            return this.selectByExample(example);
        } catch (Exception e) {
            log.error("获取任务失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void createJob(Job job) {
        job.setCreateTime(new Date());
        job.setStatus(Job.ScheduleStatus.PAUSE.getValue());
        this.save(job);
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

    @Override
    @Transactional
    public void updateJob(Job job) {
        ScheduleUtils.updateScheduleJob(scheduler, job);
        this.updateNotNull(job);
    }

    @Override
    @Transactional
    public void deleteJobs(String[] jobIds) {
        List<String> list = Arrays.asList(jobIds);
        list.forEach(jobId -> ScheduleUtils.deleteScheduleJob(scheduler, Long.valueOf(jobId)));
        this.batchDelete(list, "jobId", Job.class);
    }

    @Override
    @Transactional
    public int updateBatch(String jobIds, String status) {
        List<String> list = Arrays.asList(jobIds.split(","));
        Example example = new Example(Job.class);
        example.createCriteria().andIn("jobId", list);
        Job job = new Job();
        job.setStatus(status);
        return this.jobMapper.updateByExampleSelective(job, example);
    }

    @Override
    @Transactional
    public void run(String jobIds) {
        String[] list = jobIds.split(",");
        Arrays.stream(list).forEach(jobId -> ScheduleUtils.run(scheduler, this.findJob(Long.valueOf(jobId))));
    }

    @Override
    @Transactional
    public void pause(String jobIds) {
        String[] list = jobIds.split(",");
        Arrays.stream(list).forEach(jobId -> ScheduleUtils.pauseJob(scheduler, Long.valueOf(jobId)));
        this.updateBatch(jobIds, Job.ScheduleStatus.PAUSE.getValue());
    }

    @Override
    @Transactional
    public void resume(String jobIds) {
        String[] list = jobIds.split(",");
        Arrays.stream(list).forEach(jobId -> ScheduleUtils.resumeJob(scheduler, Long.valueOf(jobId)));
        this.updateBatch(jobIds, Job.ScheduleStatus.NORMAL.getValue());
    }
}
