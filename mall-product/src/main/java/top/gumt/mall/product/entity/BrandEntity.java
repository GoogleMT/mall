package top.gumt.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import top.gumt.common.valid.AddGroup;
import top.gumt.common.valid.ListValue;
import top.gumt.common.valid.UpdateGroup;
import top.gumt.common.valid.UpdateStatusGroup;

import javax.validation.constraints.*;


/**
 * 品牌
 * 
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 00:27:48
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改必须要指定品牌id",groups ={UpdateGroup.class} )
	@Null(message = "新增不能指定id",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */

	@NotBlank(message = "品牌名必须非空",groups = {UpdateGroup.class,AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotEmpty(groups = {AddGroup.class})
	@URL(message = "log必须是一个合法的URL地址",groups ={UpdateGroup.class,AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups ={AddGroup.class, UpdateStatusGroup.class})
	@ListValue(value = {0,1},groups ={AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "该字段必须是一个a-z或A-Z的字母",groups ={UpdateGroup.class,AddGroup.class} )
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups = {AddGroup.class})
	@Min(value = 0,message = "排序必须要大于或等于零",groups ={UpdateGroup.class,AddGroup.class})
	private Integer sort;

}
