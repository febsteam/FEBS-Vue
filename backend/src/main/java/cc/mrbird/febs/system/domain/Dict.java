package cc.mrbird.febs.system.domain;

import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@ToString
@Table(name = "t_dict")
@Excel("字典信息表")
public class Dict implements Serializable {

    private static final long serialVersionUID = 7780820231535870010L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "DICT_ID")
    private Long dictId;

    @Column(name = "KEYY")
    @NotBlank(message = "{required}")
    @Size(max = 10, message = "{noMoreThan}")
    @ExcelField(value = "键")
    private String keyy;

    @Column(name = "VALUEE")
    @NotBlank(message = "{required}")
    @Size(max = 20, message = "{noMoreThan}")
    @ExcelField(value = "值")
    private String valuee;

    @Column(name = "TABLE_NAME")
    @NotBlank(message = "{required}")
    @Size(max = 20, message = "{noMoreThan}")
    @ExcelField(value = "表名")
    private String tableName;

    @Column(name = "FIELD_NAME")
    @NotBlank(message = "{required}")
    @Size(max = 20, message = "{noMoreThan}")
    @ExcelField(value = "字段名")
    private String fieldName;

}