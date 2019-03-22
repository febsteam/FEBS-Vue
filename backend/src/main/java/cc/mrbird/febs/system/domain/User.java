package cc.mrbird.febs.system.domain;

import cc.mrbird.febs.common.converter.TimeConverter;
import cc.mrbird.febs.common.domain.RegexpConstant;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Table(name = "t_user")
@Excel("用户信息表")
public class User implements Serializable {

    private static final long serialVersionUID = -4852732617765810959L;
    /**
     * 账户状态
     */
    public static final String STATUS_VALID = "1";

    public static final String STATUS_LOCK = "0";

    public static final String DEFAULT_AVATAR = "default.jpg";

    /**
     * 性别
     */
    public static final String SEX_MALE = "0";

    public static final String SEX_FEMALE = "1";

    public static final String SEX_UNKNOW = "2";

    // 默认密码
    public static final String DEFAULT_PASSWORD = "1234qwer";

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USERNAME")
    @Size(min = 4, max = 10, message = "{range}")
    @ExcelField(value = "用户名")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "DEPT_ID")
    private Long deptId;

    @Transient
    @ExcelField(value = "部门")
    private String deptName;

    @Column(name = "EMAIL")
    @Size(max = 50, message = "{noMoreThan}")
    @Email(message = "{email}")
    @ExcelField(value = "邮箱")
    private String email;

    @Column(name = "MOBILE")
    @Pattern(regexp = RegexpConstant.MOBILE_REG, message = "{mobile}")
    @ExcelField(value = "手机号")
    private String mobile;

    @Column(name = "STATUS")
    @NotBlank(message = "{required}")
    @ExcelField(value = "状态", writeConverterExp = "0=锁定,1=有效")
    private String status;

    @Column(name = "CREATE_TIME")
    @ExcelField(value = "创建时间", writeConverter = TimeConverter.class)
    private Date createTime;

    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

    @Column(name = "LAST_LOGIN_TIME")
    @ExcelField(value = "最后登录时间", writeConverter = TimeConverter.class)
    private Date lastLoginTime;

    @Column(name = "SSEX")
    @NotBlank(message = "{required}")
    @ExcelField(value = "性别", writeConverterExp = "0=男,1=女,2=保密")
    private String ssex;

    @Column(name = "DESCRIPTION")
    @Size(max = 100, message = "{noMoreThan}")
    @ExcelField(value = "个人描述")
    private String description;

    @Column(name = "AVATAR")
    private String avatar;

    @Transient
    @NotBlank(message = "{required}")
    private String roleId;
    @Transient
    @ExcelField(value = "角色")
    private String roleName;

    // 排序字段
    @Transient
    private String sortField;

    // 排序规则 ascend 升序 descend 降序
    @Transient
    private String sortOrder;

    @Transient
    private String createTimeFrom;
    @Transient
    private String createTimeTo;

    @Transient
    private String id;

    /**
     * shiro-redis v3.1.0 必须要有 getAuthCacheKey()或者 getId()方法
     * # Principal id field name. The field which you can get unique id to identify this principal.
     * # For example, if you use UserInfo as Principal class, the id field maybe userId, userName, email, etc.
     * # Remember to add getter to this id field. For example, getUserId(), getUserName(), getEmail(), etc.
     * # Default value is authCacheKey or id, that means your principal object has a method called "getAuthCacheKey()" or "getId()"
     *
     * @return userId as Principal id field name
     */
    public Long getAuthCacheKey() {
        return userId;
    }
}