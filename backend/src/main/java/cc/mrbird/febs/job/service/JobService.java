package cc.mrbird.febs.job.service;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.service.IService;
import cc.mrbird.febs.job.domain.Job;

import java.util.List;

public interface JobService extends IService<Job> {

    Job findJob(Long jobId);

    List<Job> findJobs(QueryRequest request, Job job);

    void createJob(Job job);

    void updateJob(Job job);

    void deleteJobs(String[] jobIds);

    int updateBatch(String jobIds, String status);

    void run(String jobIds);

    void pause(String jobIds);

    void resume(String jobIds);

}
