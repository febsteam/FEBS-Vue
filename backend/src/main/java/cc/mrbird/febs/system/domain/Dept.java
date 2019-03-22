package cc.mrbird.febs.system.domain;

import cc.mrbird.febs.common.converter.TimeConverter;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "t_dept")
@Excel("部门信息表")
public class Dept implements Serializable {

    private static final long serialVersionUID = -7790334862410409053L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "DEPT_ID")
    private Long deptId;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "DEPT_NAME")
    @NotBlank(message = "{required}")
    @Size(max = 20, message = "{noMoreThan}")
    @ExcelField(value = "部门名称")
    private String deptName;

    @Column(name = "ORDER_NUM")
    private Double orderNum;

    @Column(name = "CREATE_TIME")
    @ExcelField(value = "创建时间", writeConverter = TimeConverter.class)
    private Date createTime;

    @Column(name = "MODIFY_TIME")
    @ExcelField(value = "修改时间", writeConverter = TimeConverter.class)
    private Date modifyTime;

    @Transient
    private String createTimeFrom;
    @Transient
    private String createTimeTo;

}