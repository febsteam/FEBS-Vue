package cc.mrbird.febs.system.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "t_login_log")
@ToString
@Data
public class LoginLog {
    /**
     * 用户 ID
     */
    @Column(name = "USERNAME")
    private String username;

    /**
     * 登录时间
     */
    @Column(name = "LOGIN_TIME")
    private Date loginTime;

    /**
     * 登录地点
     */
    @Column(name = "LOCATION")
    private String location;

    @Column(name = "IP")
    private String ip;
}