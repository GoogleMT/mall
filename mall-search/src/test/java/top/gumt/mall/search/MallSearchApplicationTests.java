package top.gumt.mall.search;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.gumt.common.utils.Query;
import top.gumt.mall.search.config.ESConfig;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
public class MallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void test() {
        System.out.println(client);
    }

    @Data
    class User {
        private String userName;
        private String gender;
        private Integer age;
    }

    @Test
    void indexData() throws IOException {
        // 设置索引
        IndexRequest indexRequest = new IndexRequest("users");
        // 保存数据的ID
        indexRequest.id("1");
        // 保存数据
        User user = new User();
        user.setUserName("ZhaoMing");
        user.setGender("男");
        user.setAge(20);
        // 讲数据转为Json数据
        String jsonString = JSON.toJSONString(user);
        // 设置要保存的内容，指定数据和类型
        indexRequest.source(jsonString, XContentType.JSON);
        // 真正执行创建索引和保存数据
        IndexResponse index = client.index(indexRequest, ESConfig.COMMON_OPTIONS);
        // 也可以提取有用的响应数据
        System.out.println(index);
    }

    @Test
    void find() throws IOException {
        // 1.创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 构建检索条件
//        sourceBuilder.query();
//        sourceBuilder.from();
//        sourceBuilder.size();
//        sourceBuilder.aggregation();
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        System.out.println(sourceBuilder.toString());
        searchRequest.source(sourceBuilder);
        // 执行检索
        SearchResponse searchResponse = client.search(searchRequest, ESConfig.COMMON_OPTIONS);
        // 分析检索结果
        System.out.println(searchResponse.toString());
    }

    @Data
    class Account {
        private Integer account_number;
        private Integer balance;
        private Integer age;
        private String gender;
        private String firstname;
        private String lastname;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }

    @Test
    void searchData() throws IOException {
        // 创建索引请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引， 从哪来检索
        searchRequest.indices("bank");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 构造检索条件
//        sourceBuilder.query();
//        sourceBuilder.from();
//        sourceBuilder.size();
//        sourceBuilder.aggregation();
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        // AggregationBuilders 工具类构建AggregationBuilder
        // 构建第一个聚合条件： 按照年龄的值分布
        TermsAggregationBuilder agg1 = AggregationBuilders.terms("agg1").field("age").size(10);
        // 参数为AggregationBuilder
        sourceBuilder.aggregation(agg1);
        // 构建第二个聚合条件，平均薪资
        AvgAggregationBuilder agg2 = AggregationBuilders.avg("agg2").field("balance");
        sourceBuilder.aggregation(agg2);

        System.out.println("检索条件" + sourceBuilder.toString());
        searchRequest.source(sourceBuilder);
        // 执行检索
        SearchResponse searchResponse = client.search(searchRequest, ESConfig.COMMON_OPTIONS);
        // 分析响应结果
        System.out.println(searchResponse.toString());

        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit: hits1) {
            hit.getId();
            hit.getIndex();
            String sourceAsString = hit.getSourceAsString();
//            Account account = JSON.parseObject(sourceAsString, Account.class);
//            System.out.println(account);
            Map map = JSON.parseObject(sourceAsString, Map.class);
            System.out.println(map);
        }

        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println(keyAsString);
        }
    }
}
