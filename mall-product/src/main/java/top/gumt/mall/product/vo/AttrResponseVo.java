package top.gumt.mall.product.vo;

import lombok.Data;

@Data
public class AttrResponseVo extends AttrVo {
    /**
     *  "cateLogName": "手机/数码/手机", // 所属分类名字
     *  "groupName": "主题", // 所属分组名字
     */
    private String catelogName;
    private String groupName;

    private Long[] catelogPath;
}
