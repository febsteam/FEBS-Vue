package cc.mrbird.febs.system.domain;

import cc.mrbird.febs.common.converter.TimeConverter;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "t_log")
@Excel("系统日志表")
public class SysLog implements Serializable {

    private static final long serialVersionUID = -8878596941954995444L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME")
    @ExcelField(value = "操作人")
    private String username;

    @Column(name = "OPERATION")
    @ExcelField(value = "操作描述")
    private String operation;

    @Column(name = "TIME")
    @ExcelField(value = "耗时（毫秒）")
    private Long time;

    @Column(name = "METHOD")
    @ExcelField(value = "执行方法")
    private String method;

    @Column(name = "PARAMS")
    @ExcelField(value = "方法参数")
    private String params;

    @Column(name = "IP")
    @ExcelField(value = "IP地址")
    private String ip;

    @Column(name = "CREATE_TIME")
    @ExcelField(value = "操作时间", writeConverter = TimeConverter.class)
    private Date createTime;

    @Transient
    private String createTimeFrom;
    @Transient
    private String createTimeTo;

    @Column(name = "LOCATION")
    @ExcelField(value = "操作地点")
    private String location;

}