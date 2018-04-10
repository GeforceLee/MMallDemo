package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.util.RedisShardedPoolUtil;
import com.mmall.vo.OrderVo;
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
 * Copyright (C), 2018, GeforceLee
 *
 * @author: geforce
 * @Date: 2018/4/6 下午10:34
 */
@Controller
@RequestMapping("/manage/order")
@Api(tags = "管理订单",description = "订单管理接口")
public class OrderManageController {


    @Autowired
    private IUserService iUserService;


    @Autowired
    private IOrderService iOrderService;


    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name = "pageNum",paramType = "query"),
            @ApiImplicitParam(value = "每页大小",name = "pageSize",paramType = "query"),
    })
    public ServerResponse<PageInfo> orderList(@ApiIgnore HttpServletRequest request,
                                              @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageList(pageNum,pageSize);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }


    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单号",name = "orderNo",paramType = "query")
    })
    public ServerResponse<OrderVo> orderDetail(@ApiIgnore HttpServletRequest request,
                                               @RequestParam(value = "orderNo",defaultValue = "1") Long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageDetail(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }



    @RequestMapping(value = "search.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("订单搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单号",name = "orderNo",paramType = "query"),
            @ApiImplicitParam(value = "页码",name = "pageNum",paramType = "query"),
            @ApiImplicitParam(value = "每页大小",name = "pageSize",paramType = "query"),
    })
    public ServerResponse<PageInfo> orderSearch(@ApiIgnore HttpServletRequest request,
                                               @RequestParam(value = "orderNo",defaultValue = "1") Long orderNo,
                                               @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                               @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSearch(orderNo,pageNum,pageSize);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }



    @RequestMapping(value = "send_goods.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("订单发货")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单号",name = "orderNo",paramType = "query")
    })
    public ServerResponse<String> orserSendGoods(@ApiIgnore HttpServletRequest request,
                                               Long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSendGood(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }
}
