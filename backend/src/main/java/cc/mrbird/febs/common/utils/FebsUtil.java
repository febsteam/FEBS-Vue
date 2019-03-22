package cc.mrbird.febs.common.utils;

import cc.mrbird.febs.common.authentication.JWTUtil;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.function.CacheSelector;
import cc.mrbird.febs.common.service.CacheService;
import cc.mrbird.febs.system.domain.User;
import cc.mrbird.febs.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * FEBS工具类
 */
@Slf4j
public class FebsUtil {

    /**
     * 缓存查询摸板，先查缓存，如果缓存查询失败再从数据库查询
     *
     * @param cacheSelector    查询缓存的方法
     * @param databaseSelector 数据库查询方法
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T selectCacheByTemplate(CacheSelector<?> cacheSelector, Supplier<?> databaseSelector) {
        try {
            // 先查 Redis缓存
            log.info("query data from redis ······");
            return (T) cacheSelector.select();
        } catch (Exception e) {
            // 数据库查询
            log.error("redis error：", e);
            log.info("query data from database ······");
            return (T) databaseSelector.get();
        }
    }

    /**
     * 获取当前操作用户
     *
     * @return 用户信息
     */
    public static User getCurrentUser() {
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        String username = JWTUtil.getUsername(token);
        UserService userService = SpringContextUtil.getBean(UserService.class);
        CacheService cacheService = SpringContextUtil.getBean(CacheService.class);

        return selectCacheByTemplate(() -> cacheService.getUser(username), () -> userService.findByName(username));
    }

    /**
     * token 加密
     *
     * @param token token
     * @return 加密后的 token
     */
    public static String encryptToken(String token) {
        try {
            EncryptUtil encryptUtil = new EncryptUtil(FebsConstant.TOKEN_CACHE_PREFIX);
            return encryptUtil.encrypt(token);
        } catch (Exception e) {
            log.info("token加密失败：", e);
            return null;
        }
    }

    /**
     * token 解密
     *
     * @param encryptToken 加密后的 token
     * @return 解密后的 token
     */
    public static String decryptToken(String encryptToken) {
        try {
            EncryptUtil encryptUtil = new EncryptUtil(FebsConstant.TOKEN_CACHE_PREFIX);
            return encryptUtil.decrypt(encryptToken);
        } catch (Exception e) {
            log.info("token解密失败：", e);
            return null;
        }
    }

    /**
     * 驼峰转下划线
     *
     * @param value 待转换值
     * @return 结果
     */
    private static String camelToUnderscore(String value) {
        if (StringUtils.isBlank(value))
            return "";
        String[] arr = StringUtils.splitByCharacterTypeCamelCase(value);
        if (arr.length == 0)
            return "";
        StringBuilder result = new StringBuilder();
        IntStream.range(0, arr.length).forEach(i -> {
            if (i != arr.length - 1)
                result.append(arr[i]).append("_");
            else
                result.append(arr[i]);
        });
        return StringUtils.lowerCase(result.toString());
    }

    /**
     * 处理排序逻辑
     *
     * @param request     QueryRequest
     * @param example     Example
     * @param defaultSort 默认排序的字段
     */
    public static void handleSort(QueryRequest request, Example example, String defaultSort) {
        if (StringUtils.isNotBlank(request.getSortField())
                && StringUtils.isNotBlank(request.getSortOrder())
                && !StringUtils.equalsIgnoreCase(request.getSortField(), "undefined")
                && !StringUtils.equalsIgnoreCase(request.getSortOrder(), "undefined")) {
            String orderCase;
            if (StringUtils.equals(request.getSortOrder(), "ascend"))
                orderCase = "asc";
            else
                orderCase = "desc";
            String orderField = camelToUnderscore(request.getSortField());
            example.setOrderByClause(orderField + " " + orderCase);
        } else {
            example.setOrderByClause(defaultSort);
        }
    }
}
