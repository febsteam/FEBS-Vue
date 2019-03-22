package cc.mrbird.febs.common.aspect;

import cc.mrbird.febs.common.authentication.JWTUtil;
import cc.mrbird.febs.common.properties.FebsProperties;
import cc.mrbird.febs.common.utils.HttpContextUtil;
import cc.mrbird.febs.common.utils.IPUtil;
import cc.mrbird.febs.system.domain.SysLog;
import cc.mrbird.febs.system.service.LogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * AOP 记录用户操作日志
 *
 * @author MrBird
 * @link https://mrbird.cc/Spring-Boot-AOP%20log.html
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private FebsProperties febsProperties;

    @Autowired
    private LogService logService;

    @Pointcut("@annotation(cc.mrbird.febs.common.annotation.Log)")
    public void pointcut() {
        // do nothing
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws JsonProcessingException {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        try {
            // 执行方法
            result = point.proceed();
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
        // 获取 request
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        // 设置 IP 地址
        String ip = IPUtil.getIpAddr(request);
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        if (febsProperties.isOpenAopLog()) {
            // 保存日志
            String token = (String) SecurityUtils.getSubject().getPrincipal();
            String username = JWTUtil.getUsername(token);

            SysLog log = new SysLog();
            log.setUsername(username);
            log.setIp(ip);
            log.setTime(time);
            logService.saveLog(point, log);
        }
        return result;
    }
}
