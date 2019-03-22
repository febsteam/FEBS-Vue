package cc.mrbird.febs.system.service;

import cc.mrbird.febs.common.service.IService;
import cc.mrbird.febs.system.domain.LoginLog;

public interface LoginLogService extends IService<LoginLog> {

    void saveLoginLog (LoginLog loginLog);
}
