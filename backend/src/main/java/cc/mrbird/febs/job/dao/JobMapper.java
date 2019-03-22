package cc.mrbird.febs.job.dao;


import cc.mrbird.febs.common.config.MyMapper;
import cc.mrbird.febs.job.domain.Job;

import java.util.List;

public interface JobMapper extends MyMapper<Job> {
	
	List<Job> queryList();
}