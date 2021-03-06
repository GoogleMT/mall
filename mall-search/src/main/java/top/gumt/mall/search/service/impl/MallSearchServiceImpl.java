package top.gumt.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.gumt.common.to.to.SkuEsModel;
import top.gumt.common.utils.R;
import top.gumt.mall.search.config.ESConfig;
import top.gumt.mall.search.constant.EsConstant;
import top.gumt.mall.search.feign.ProductFeignService;
import top.gumt.mall.search.service.MallSearchService;
import top.gumt.mall.search.vo.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("mallSearchService")
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam param) {
        // ??????????????????????????????DSL??????
        SearchResult result = null;
        // ??????????????????
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            // ??????????????????
            SearchResponse search = client.search(searchRequest, ESConfig.COMMON_OPTIONS);
            result = buildSearchResult(param, search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    /**
     * ??????????????????
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        // ??????DSL
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         * ??????
         */
        // 1.??????bool-query
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 1.1 bool-must
        if(!StringUtils.isEmpty(param.getKeyword())) {
            queryBuilder.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        // 1.2 bool-fliter
        // 1.2.1 catalogId
        if (null != param.getCatalog3Id()) {
            queryBuilder.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));;
        }
        // 1.2.2 brandId
        if (null != param.getBrandId() && param.getBrandId().size() > 0) {
            queryBuilder.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        // 1.2.3 attrs
        if(null != param.getAttrs() && param.getAttrs().size() > 0) {
            param.getAttrs().forEach(item -> {
                // attrs=1_5???:8???&2_16G:8G
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                // attrs 1_5???:8???
                String[] s = item.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));

                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                queryBuilder.filter(nestedQueryBuilder);
            });
        }

        // 1.2.4 hasStock
        if (null != param.getHasStock()) {
            queryBuilder.filter(QueryBuilders.termQuery("hasStock",param.getHasStock() == 1));
        }

        // 1.2.5 skuPrice
        if(!StringUtils.isEmpty(param.getSkuPrice())) {
            // skuPrice ???????????? 1_500 ??? _500 ??? 500_
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] price = param.getSkuPrice().split("_");
            if(price.length == 2) {
                rangeQueryBuilder.gte(price[0]).lte(price[1]);
            } else if (price.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(price[1]);
                }
                if (param.getSkuPrice().endsWith("_")) {
                    rangeQueryBuilder.gte(price[0]);
                }
            }
            queryBuilder.filter(rangeQueryBuilder);
        }

        // ???????????????????????????
        sourceBuilder.query(queryBuilder);

        /**
         * ????????????????????????
         */
        // ??????
        if(!StringUtils.isEmpty(param.getSort())) {
            String sort = param.getSort();
            String[] sortFileds = sort.split("_");

            SortOrder sortOrder = "asc".equalsIgnoreCase(sortFileds[0]) ? SortOrder.ASC : SortOrder.DESC;

            sourceBuilder.sort(sortFileds[0], sortOrder);
        }

        // ??????
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        // ??????
        if(!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * ????????????
         */
        // 1. ???????????????????????????
        TermsAggregationBuilder brandNameAgg = AggregationBuilders.terms("brand_agg");
        brandNameAgg.field("brandId").size(50);

        // 1.1 ??????????????????-???????????????
        brandNameAgg.subAggregation(AggregationBuilders.terms("brand_Name_agg")
                .field("brandName").size(1));

        // 1.2 ?????????????????? - ??????????????????
        brandNameAgg.subAggregation(AggregationBuilders.terms("brand_img_agg")
                .field("brandImg").size(1));
        sourceBuilder.aggregation(brandNameAgg);

        // 2 ??????????????????????????????
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg");
        catalogAgg.field("catalogId").size(20);

        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));

        sourceBuilder.aggregation(catalogAgg);

        // 2 ??????????????????????????????
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        // 2.1 ????????????ID????????????
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attrAgg.subAggregation(attrIdAgg);
        // 2.1.1 ???????????????ID?????????????????????????????????
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        // 2.1.1
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        sourceBuilder.aggregation(attrAgg);

        System.out.println("?????????DSL?????? " + sourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }

    /**
     * ?????????????????????
     * @param param
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchParam param, SearchResponse response) {
        SearchResult result = new SearchResult();
        SearchHits hits = response.getHits();

        SearchHit[] subHits = hits.getHits();
        List<SkuEsModel> skuEsModels=null;
        if(subHits != null && subHits.length > 0){

            skuEsModels = Arrays.asList(subHits).stream().map(subHit -> {
                String sourceAsString = subHit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    HighlightField skuTitle = subHit.getHighlightFields().get("skuTitle");
                    String skuTitleHighLight = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(skuTitleHighLight);
                }
                return skuEsModel;
            }).collect(Collectors.toList());

        }

        //1.?????????????????????????????????
        result.setProducts(skuEsModels);

        //2.???????????????????????????????????????????????????
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        List<AttrVo> attrVos = attr_id_agg.getBuckets().stream().map(item -> {
            AttrVo attrVo = new AttrVo();
            //1.???????????????id
            long attrId = item.getKeyAsNumber().longValue();

            //2.???????????????
            String attrName = ((ParsedStringTerms) item.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();

            //3.????????????????????????
            List<String> attrValues = ((ParsedStringTerms) item.getAggregations().get("attr_value_agg")).getBuckets().stream().map(bucket -> {
                return bucket.getKeyAsString();
            }).collect(Collectors.toList());

            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(attrValues);


            return attrVo;
        }).collect(Collectors.toList());
        result.setAttrs(attrVos);

        //3.???????????????????????????????????????????????????
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        List<BrandVo> brandVos = brand_agg.getBuckets().stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            //1.??????id
            long brandId = item.getKeyAsNumber().longValue();
            //2.???????????????
            String brandName = ((ParsedStringTerms) item.getAggregations().get("brand_Name_agg")).getBuckets().get(0).getKeyAsString();
            //3.??????????????????
            String brandImag = ((ParsedStringTerms) item.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();

            brandVo.setBrandId(brandId);
            brandVo.setBrandName(brandName);
            brandVo.setBrandImg(brandImag);
            return brandVo;
        }).collect(Collectors.toList());
        result.setBrands(brandVos);

        //4.???????????????????????????????????????????????????
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        List<CatalogVo> catalogVos = catalogAgg.getBuckets().stream().map(item -> {
            CatalogVo catalogVo = new CatalogVo();
            //????????????ID
            String catalogId = item.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(catalogId));

            //???????????????
            ParsedStringTerms catalogNameAgg = item.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            return catalogVo;
        }).collect(Collectors.toList());

        result.setCatalogs(catalogVos);

        //=========??????????????????????????????===========
        //5.????????????-??????

        result.setPageNum(param.getPageNum());
        //5.????????????-????????????
        long total = hits.getTotalHits().value;
        result.setTotal(total);

        //5.????????????-?????????
        boolean flag = total % EsConstant.PRODUCT_PAGESIZE == 0;
        int totalPage=  flag ? (int)total / EsConstant.PRODUCT_PAGESIZE:((int)total / EsConstant.PRODUCT_PAGESIZE) + 1;
        result.setTotalPages(totalPage);

        ArrayList<Integer> page = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++){
            page.add(i);
        }
        result.setPageNavs(page);

        //???????????????????????????
        List<String> attrs = param.getAttrs();
        if (attrs != null && attrs.size() > 0) {
            List<SearchResult.NavVo> navVos = attrs.stream().map(attr -> {
                String[] split = attr.split("_");
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //6.1 ???????????????
                navVo.setNavValue(split[1]);
                //6.2 ????????????????????????
                try {
                    R r = productFeignService.info(Long.parseLong(split[0]));
                    if (r.getCode() == 0) {
                        AttrResponseVo attrResponseVo = JSON.parseObject(JSON.toJSONString(r.get("attr")), new TypeReference<AttrResponseVo>() {
                        });
                        navVo.setName(attrResponseVo.getAttrName());
                    }
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????", e);
                }
                //6.3 ???????????????????????????(???????????????????????????????????????)
                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://search.mall.com/list.html" + (replace.isEmpty() ? "" : "?" + replace));
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }

        // ??????
        if(null != param.getBrandId() && param.getBrandId().size() > 0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();

            navVo.setName("??????");
            R r = productFeignService.brandsInfo(param.getBrandId());
            if (r.getCode() == 0) {
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                String replace = "";
                for (BrandVo brandVo : brand) {
                    buffer.append(brandVo.getBrandName() + ";");
                    replace = replaceQueryString(param, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.mall.com/list.html?" + replace);;
            }
            navs.add(navVo);
            // TODO  ??????
        }

        return result;
    }

    private String replaceQueryString(SearchParam param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            // ??????????????????
            encode = encode.replace("+", "%2d");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param.getQueryString().replace("&" + key + "=" + encode, "");
    }
}
