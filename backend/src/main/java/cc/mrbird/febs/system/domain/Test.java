package cc.mrbird.febs.system.domain;

import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import com.wuwenze.poi.validator.EmailValidator;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_test")
@Excel("测试导入导出数据")
public class Test {
    @Column(name = "FIELD1")
    @ExcelField(value = "字段1", required = true, maxLength = 20,
            comment = "提示：必填，长度不能超过20个字符")
    private String field1;

    @Column(name = "FIELD2")
    @ExcelField(value = "字段2", required = true, maxLength = 11, regularExp = "[0-9]+",
            regularExpMessage = "必须是数字", comment = "提示: 必填，只能填写数字，并且长度不能超过11位")
    private Integer field2;

    @Column(name = "FIELD3")
    @ExcelField(value = "字段3", required = true, maxLength = 50,
            comment = "提示：必填，只能填写邮箱，长度不能超过50个字符", validator = EmailValidator.class)
    private String field3;

    @Column(name = "CREATE_TIME")
    private Date createTime;
}