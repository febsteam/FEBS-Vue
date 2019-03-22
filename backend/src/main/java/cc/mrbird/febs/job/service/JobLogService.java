package cc.mrbird.febs.job.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.service.IService;
import cc.mrbird.febs.job.domain.JobLog;

import java.util.List;

public interface JobLogService extends IService<JobLog> {

    List<JobLog> findJobLogs(QueryRequest request, JobLog jobLog);

    void saveJobLog(JobLog log);

    void deleteJobLogs(String[] jobLogIds);
}
