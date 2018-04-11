package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.util.RedisShardedPoolUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author geforce
 * @date 2018/4/2
 */
@Controller
@RequestMapping("/manage/category")
@Api(tags = "管理类别",description = "后端管理类别接口")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 增加类别
     * @param request
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("增加类别")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "类别名称",name = "categoryName",required = true,paramType = "query"),
            @ApiImplicitParam(value = "父类别id",name = "parentId",required = true,paramType = "query",defaultValue = "0")
    })
    public ServerResponse addCategory(@ApiIgnore HttpServletRequest request, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId){

        return iCategoryService.addCategory(categoryName,parentId);
    }

    /**
     * 更新类别名称
     * @param request
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "set_category_name.do",method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation("更新类别名称")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "类别id",name = "categoryId",required = true, paramType = "query"),
            @ApiImplicitParam(value = "类别名称",name = "categoryName",required = true, paramType = "query")
    })
    public ServerResponse setCategoryName(@ApiIgnore HttpServletRequest request,Integer categoryId,String categoryName) {

        return iCategoryService.updateCategoryName(categoryId,categoryName);
    }

    /**
     * 获取平行子类别
     * @param request
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_category.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取平行子类别")
    @ApiImplicitParam(value = "类别id",name = "categoryId",defaultValue = "0",paramType = "query")
    public ServerResponse getChildernParallelCategory(@ApiIgnore HttpServletRequest request,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){

        //查询子节点的category信息,不递归
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    /**
     * 递归获取所有子节点
     * @param request
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("递归获取所有子节点")
    @ApiImplicitParam(value = "类别id",name = "categoryId",defaultValue = "0",paramType = "query")
    public ServerResponse getCategoryAndDeepChildrenCategory(@ApiIgnore HttpServletRequest request,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){

        //查询当前节点的category信息,递归子节点
        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }

}
