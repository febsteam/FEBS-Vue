package cc.mrbird.febs.job.domain;


import cc.mrbird.febs.common.converter.TimeConverter;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Table(name = "t_job_log")
@Excel("调度日志信息表")
public class JobLog implements Serializable {

    private static final long serialVersionUID = -7114915445674333148L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "LOG_ID")
    private Long logId;

    @Column(name = "JOB_ID")
    private Long jobId;

    @Column(name = "BEAN_NAME")
    @ExcelField(value = "Bean名称")
    private String beanName;

    @Column(name = "METHOD_NAME")
    @ExcelField(value = "方法名称")
    private String methodName;

    @Column(name = "PARAMS")
    @ExcelField(value = "方法参数")
    private String params;

    @Column(name = "STATUS")
    @ExcelField(value = "状态", writeConverterExp = "0=成功,1=失败")
    private String status;

    @Column(name = "ERROR")
    @ExcelField(value = "异常信息")
    private String error;

    @Column(name = "TIMES")
    @ExcelField(value = "耗时（毫秒）")
    private Long times;

    @Column(name = "CREATE_TIME")
    @ExcelField(value = "执行时间", writeConverter = TimeConverter.class)
    private Date createTime;

    @Transient
    private String createTimeFrom;
    @Transient
    private String createTimeTo;

}