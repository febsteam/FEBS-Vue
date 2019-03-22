package cc.mrbird.febs.common.runner;

import cc.mrbird.febs.common.exception.RedisConnectException;
import cc.mrbird.febs.common.service.CacheService;
import cc.mrbird.febs.system.dao.UserMapper;
import cc.mrbird.febs.system.domain.User;
import cc.mrbird.febs.system.manager.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 缓存初始化
 */
@Slf4j
@Component
public class CacheInitRunner implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserManager userManager;

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void run(String... args) {
        try {
            log.info("Redis连接中 ······");
            cacheService.testConnect();

            log.info("缓存初始化 ······");
            log.info("缓存用户数据 ······");
            List<User> list = this.userMapper.findUserDetail(null);
            for (User user : list) {
                userManager.loadUserRedisCache(user);
            }
        } catch (Exception e) {
            log.error("缓存初始化失败，{}", e.getMessage());
            log.error(" ____   __    _   _ ");
            log.error("| |_   / /\\  | | | |");
            log.error("|_|   /_/--\\ |_| |_|__");
            log.error("                        ");
            log.error("FEBS启动失败              ");
            if (e instanceof RedisConnectException)
                log.error("Redis连接异常，请检查Redis连接配置并确保Redis服务已启动");
            // 关闭 FEBS
            context.close();
        }
    }
}
