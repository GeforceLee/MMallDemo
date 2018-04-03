package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author geforce
 * @date 2018/4/3
 */
@Controller
@RequestMapping("/product")
@Api(tags = "前台产品",description = "用户产品接口")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /**
     * 产品详情
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("产品详情")
    @ApiImplicitParam(value = "产品id",name = "productId",paramType = "query")
    public ServerResponse detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }


    /**
     * 产品列表
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(value = "关键词",name = "keyword",paramType = "query"),
            @ApiImplicitParam(value = "类别id",name = "categoryId",paramType = "query"),
            @ApiImplicitParam(value = "第几页",name = "pageNum",paramType = "query"),
            @ApiImplicitParam(value = "每页大小",name = "pageSize",paramType = "query"),
            @ApiImplicitParam(value = "排序",name = "orderBy",paramType = "query",allowableValues = ",price_asc,price_desc")
    })
    public ServerResponse list(String keyword,
                               Integer categoryId,
                               @RequestParam(value ="pageNum",defaultValue = "1") int pageNum,
                               @RequestParam(value ="pageSize",defaultValue = "10") int pageSize,
                               @RequestParam(value ="orderBy",defaultValue = "") String orderBy) {
        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }


}
