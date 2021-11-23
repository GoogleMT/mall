package top.gumt.mall.product.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.gumt.mall.product.entity.CategoryEntity;
import top.gumt.mall.product.service.CategoryService;
import top.gumt.mall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;


    @GetMapping(value = {"/","/index.html"})
    public String indexPage(Model model){
        //1. 查询出所有的一级分类
        List<CategoryEntity> categoryEntityList=categoryService.getLevel1Categories();
        model.addAttribute("categories",categoryEntityList);
        return  "index";
    }

    @ResponseBody
    @GetMapping("index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson(){

        Map<String, List<Catelog2Vo>> map=categoryService.getCatelogJson();
        return map;
    }
}
