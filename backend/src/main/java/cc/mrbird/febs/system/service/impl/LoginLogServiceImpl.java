package cc.mrbird.febs.system.service.impl;

import cc.mrbird.febs.common.service.impl.BaseService;
import cc.mrbird.febs.common.utils.AddressUtil;
import cc.mrbird.febs.common.utils.HttpContextUtil;
import cc.mrbird.febs.common.utils.IPUtil;
import cc.mrbird.febs.system.domain.LoginLog;
import cc.mrbird.febs.system.service.LoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service("loginLogService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LoginLogServiceImpl extends BaseService<LoginLog> implements LoginLogService {

    @Override
    @Transactional
    public void saveLoginLog(LoginLog loginLog) {
        loginLog.setLoginTime(new Date());
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        String ip = IPUtil.getIpAddr(request);
        loginLog.setIp(ip);
        loginLog.setLocation(AddressUtil.getCityInfo(ip));
        this.save(loginLog);
    }
}
