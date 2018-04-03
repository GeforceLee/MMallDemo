package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

/**
 * @author geforce
 * @date 2018/4/3
 */
@Controller
@RequestMapping("/manage/product")
@Api(tags = "管理产品",description = "产品相关接口")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    /**
     * 产品新增或更新
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("产品新增或更新")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "产品id",name="id",paramType = "query"),
            @ApiImplicitParam(value = "类别id",name="categoryId",paramType = "query"),
            @ApiImplicitParam(value = "名称",name="name",paramType = "query"),
            @ApiImplicitParam(value = "子标题",name="subtitle",paramType = "query"),
            @ApiImplicitParam(value = "主图片",name="mainImage",paramType = "query"),
            @ApiImplicitParam(value = "字图片",name="subImages",paramType = "query"),
            @ApiImplicitParam(value = "详情",name="detail",paramType = "query"),
            @ApiImplicitParam(value = "价格",name="price",paramType = "query"),
            @ApiImplicitParam(value = "库存",name="stock",paramType = "query"),
            @ApiImplicitParam(value = "状态",name="status",paramType = "query")
    })
    public ServerResponse productSave(@ApiIgnore HttpSession session,@ApiIgnore Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.saveOrUpdateUpdate(product);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * 设置产品状态
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation("设置产品状态")
    @ApiImplicitParams({
        @ApiImplicitParam(value = "产品id",name = "productId",paramType = "query"),
        @ApiImplicitParam(value = "状态",name = "status",paramType = "query")
    })
    public ServerResponse setSaleStatus(@ApiIgnore HttpSession session, Integer productId,Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId,status);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * 产品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do",method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation("产品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "产品id",name = "productId",paramType = "query")
    })
    public ServerResponse getDetail(@ApiIgnore HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId,1);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

}
