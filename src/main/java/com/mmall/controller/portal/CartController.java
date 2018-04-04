package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
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
@RequestMapping("/cart")
@Api(tags = "用户购物车",description = "购物车接口")
public class CartController {

    @Autowired
    private ICartService iCartService;


    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("购物车列表")
    public ServerResponse list(@ApiIgnore HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping(value = "add.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("加入购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "产品id",name = "productId",required = true,paramType = "query"),
            @ApiImplicitParam(value = "数量",name = "count",required = true,paramType = "query"),
    })
    public ServerResponse add(@ApiIgnore HttpSession session,Integer count,Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }



    @RequestMapping(value = "update.do",method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation("更新产品数量")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "产品id",name = "productId",required = true,paramType = "query"),
            @ApiImplicitParam(value = "数量",name = "count",required = true,paramType = "query"),
    })
    public ServerResponse update(@ApiIgnore HttpSession session,Integer count,Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }



    @RequestMapping(value = "delete_product.do",method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation("删除产品")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "产品id列表",name = "productIds",required = true,paramType = "query"),
    })
    public ServerResponse deleteProduct(@ApiIgnore HttpSession session,String productIds) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iCartService.delete(user.getId(),productIds);
    }


    @RequestMapping(value = "select_all.do",method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation("全选")
    public ServerResponse selectAll(@ApiIgnore HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),null,Const.Cart.CHECKED);
    }


    @RequestMapping(value = "unselect_all.do",method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation("全不选")
    public ServerResponse unSelectAll(@ApiIgnore HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),null,Const.Cart.UN_CHECKED);
    }

    @RequestMapping(value = "select.do",method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation("选中产品")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "产品id",name = "productId",required = true,paramType = "query"),
    })
    public ServerResponse select(@ApiIgnore HttpSession session,Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Const.Cart.CHECKED);
    }


    @RequestMapping(value = "unselect.do",method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation("选取消选择产品")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "产品id",name = "productId",required = true,paramType = "query"),
    })
    public ServerResponse unSelect(@ApiIgnore HttpSession session,Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }



    @RequestMapping(value = "get_cart_product_count.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("购物车产品数量")
    public ServerResponse getCartProductCount(@ApiIgnore HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
