package cc.mrbird.febs.sms.util;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 接口调用
 *
 */
public class MsgActivityTimer extends QuartzJobBean {
	private static Logger LOGGER = LoggerFactory.getLogger(MsgActivityTimer.class);

	/**
	 * 短信接口长链接，定时进行链路检查
	 */
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		LOGGER.info("×××××××××××××开始链路检查××××××××××××××");
		int count = 0;
		boolean result = MsgContainer.activityTestISMG();
		while (!result) {
			count++;
			result = MsgContainer.activityTestISMG();
			if (count >= (MsgConfig.getConnectCount() - 1)) {// 如果再次链路检查次数超过两次则终止连接
				break;
			}
		}
		LOGGER.info("×××××××××××××链路检查结束××××××××××××××");
	}
}
