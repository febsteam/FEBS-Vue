package cc.mrbird.febs.job.domain;

import cc.mrbird.febs.common.annotation.IsCron;
import cc.mrbird.febs.common.converter.TimeConverter;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Table(name = "t_job")
@Excel("定时任务信息表")
public class Job implements Serializable {

    private static final long serialVersionUID = 400066840871805700L;

    /**
     * 任务调度参数 key
     */
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL("0"),
        /**
         * 暂停
         */
        PAUSE("1");

        private String value;

        ScheduleStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "JOB_ID")
    private Long jobId;

    @Column(name = "BEAN_NAME")
    @NotBlank(message = "{required}")
    @Size(max = 50, message = "{noMoreThan}")
    @ExcelField(value = "Bean名称")
    private String beanName;

    @Column(name = "METHOD_NAME")
    @NotBlank(message = "{required}")
    @Size(max = 50, message = "{noMoreThan}")
    @ExcelField(value = "方法名称")
    private String methodName;

    @Column(name = "PARAMS")
    @Size(max = 50, message = "{noMoreThan}")
    @ExcelField(value = "方法参数")
    private String params;

    @Column(name = "CRON_EXPRESSION")
    @NotBlank(message = "{required}")
    @IsCron(message = "{invalid}")
    @ExcelField(value = "Cron表达式")
    private String cronExpression;

    @Column(name = "STATUS")
    @ExcelField(value = "状态", writeConverterExp = "0=正常,1=暂停")
    private String status;

    @Column(name = "REMARK")
    @Size(max = 100, message = "{noMoreThan}")
    @ExcelField(value = "备注")
    private String remark;

    @Column(name = "CREATE_TIME")
    @ExcelField(value = "创建时间", writeConverter = TimeConverter.class)
    private Date createTime;

    @Transient
    private String createTimeFrom;
    @Transient
    private String createTimeTo;

}