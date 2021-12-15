package top.gumt.mall.search.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import top.gumt.mall.search.service.MallSearchService;
import top.gumt.mall.search.vo.SearchParam;
import top.gumt.mall.search.vo.SearchResult;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {
//
    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request){
        String queryString = request.getQueryString();
        param.setQueryString(queryString);

        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }
}
