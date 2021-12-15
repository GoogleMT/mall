package top.gumt.mall.search.service;

import top.gumt.mall.search.vo.SearchParam;
import top.gumt.mall.search.vo.SearchResult;

public interface MallSearchService {
    /**
     * 搜索服务
     * @param param
     * @return
     */
    SearchResult search(SearchParam param);
}
