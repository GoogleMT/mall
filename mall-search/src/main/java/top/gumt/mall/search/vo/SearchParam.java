package top.gumt.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 检索参数封装
 */
@Data
public class SearchParam {
    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 三级分类ID
     */
    private Long catalog3Id;

    /**
     * 排序条件 sort=price/salecount/hotscore_desc/asc
     */
    private String sort;

    /**
     * 仅仅显示有货
     */
    private Integer hasStock;

    /**
     * 价格区间
     */
    private String skuPrice;

    /**
     * 品牌ID 可以多选
     */
    private List<Long> brandId;

    /**
     * 按照属性进行筛选
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生所有查询属性
     */
    private String queryString;
}
