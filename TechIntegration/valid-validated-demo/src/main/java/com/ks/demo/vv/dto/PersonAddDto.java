package com.ks.demo.vv.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ks.demo.vv.config.ValidatedGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PersonAddDto {
    //使用了group的，限定只有在该个标记的情况下才会使得当前校验生效
    //在Controller层使用：@Validated(ValidatedGroup.Update.class) @RequestBody PersonAddDto personAddDto
    //含义：限定在更新和删除时，必须携带ID
    @NotNull(groups = {ValidatedGroup.Update.class, ValidatedGroup.Delete.class}, message = "更新操作时不允许id为空")
    private Long id;

    @NotBlank(message = "请输入名称")
    @Size(message = "名称字符长度在{min}到{max}之间", min = 1, max = 10)
    private String name;

    @NotNull(message = "请输入年龄")
    @Range(message = "年龄范围为 {min} 到 {max} 之间", min = 1, max = 100)
    private Integer age;

    @Max(value = 2, message = "性别限定最大值为2")
    @Min(value = 1, message = "性别限定最小值为1")
    @Positive(message = "性别字段只可能为正数")
    private Integer gender;

    @PastOrPresent(message = "出生日期一定在当前之间之前")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;

    private String address;

    @Pattern(regexp = "/^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$/", message = "手机号格式错误")
    private String tel;

    @Email(message = "邮箱格式错误")
    private String email;

    @DecimalMax(value = "99999", message = "工资上限为99999")
    //@DecimalMin(value = "99", message = "工资下限为99")
    @PositiveOrZero
    private BigDecimal salary;

}
