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
@Table(name = "t_menu")
@Excel("菜单信息表")
public class Menu implements Serializable {

    private static final long serialVersionUID = 7187628714679791771L;

    public static final String TYPE_MENU = "0";

    public static final String TYPE_BUTTON = "1";

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "MENU_NAME")
    @NotBlank(message = "{required}")
    @Size(max = 10, message = "{noMoreThan}")
    @ExcelField(value = "名称")
    private String menuName;

    @Column(name = "PATH")
    @Size(max = 50, message = "{noMoreThan}")
    @ExcelField(value = "地址")
    private String path;

    @Column(name = "COMPONENT")
    @Size(max = 100, message = "{noMoreThan}")
    @ExcelField(value = "对应Vue组件")
    private String component;

    @Column(name = "PERMS")
    @Size(max = 50, message = "{noMoreThan}")
    @ExcelField(value = "权限")
    private String perms;

    @Column(name = "ICON")
    @ExcelField(value = "图标")
    private String icon;

    @Column(name = "TYPE")
    @NotBlank(message = "{required}")
    @ExcelField(value = "类型", writeConverterExp = "0=按钮,1=菜单")
    private String type;

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