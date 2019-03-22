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
@Table(name = "t_role")
@Excel("角色信息表")
public class Role implements Serializable {

    private static final long serialVersionUID = -1714476694755654924L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ROLE_NAME")
    @NotBlank(message = "{required}")
    @Size(max = 10, message = "{noMoreThan}")
    @ExcelField(value = "角色名称")
    private String roleName;

    @Column(name = "REMARK")
    @Size(max = 50, message = "{noMoreThan}")
    @ExcelField(value = "角色描述")
    private String remark;

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

    @Transient
    private String menuId;

}