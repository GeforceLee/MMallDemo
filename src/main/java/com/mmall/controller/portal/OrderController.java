package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * @author geforce
 * @date 2018/4/4
 */
@Controller
@RequestMapping("/order")
@Api(tags = "用户订单",description = "订单接口")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;



    @RequestMapping(value = "caeate.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("创建订单")
    @ApiImplicitParam(value = "收货地址id",name = "shippingId",paramType = "query")
    public ServerResponse caeate(@ApiIgnore HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }

        return iOrderService.cratetOrder(user.getId(),shippingId);
    }


    @RequestMapping(value = "cancel.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("取消订单")
    @ApiImplicitParam(value = "订单号",name = "orderNo",paramType = "query")
    public ServerResponse cancel(@ApiIgnore HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }

        return iOrderService.canecel(user.getId(),orderNo);
    }


    @RequestMapping(value = "get_order_cart_product.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取订单中购物车的商品")
    public ServerResponse getOrderCartProduct(@ApiIgnore HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }

        return iOrderService.getOrderCartProduct(user.getId());
    }


    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("订单详情")
    @ApiImplicitParam(value = "订单号",name = "orderNo",paramType = "query")
    public ServerResponse detail(@ApiIgnore HttpSession session,Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }

        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }


    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name = "pageNum",paramType = "query"),
            @ApiImplicitParam(value = "每页大小",name = "pageSize",paramType = "query"),
    })
    public ServerResponse list(@ApiIgnore HttpSession session,
                               @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }

        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }




    @RequestMapping(value = "pay.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("查询支付宝状态")
    @ApiImplicitParam(value = "订单号",name = "orderNo",paramType = "query")
    public ServerResponse apy(@ApiIgnore HttpSession session, Long orderNo,@ApiIgnore HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    @ApiIgnore
    public Object alipayCallback(HttpServletRequest request) {
        Map<String ,String > params = Maps.newHashMap();

        Map<String,String[]> requestParams = request.getParameterMap();

        for (Iterator<String> iter = requestParams.keySet().iterator();iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);

            String valueStr = "";

            for (int i = 0; i < values.length; i++) {
                valueStr = (i==values.length-1)?valueStr+values[i] : valueStr+values[i]+",";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝回调,sign:{} 交易状态:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());


        //验证回调的正确性, 并且阻止重复回调
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params,Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

            if (!alipayRSACheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求,支付宝验证失败");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
        }


        //TODO 验证各种参数


        ServerResponse response = iOrderService.aliCallBack(params);
        if (response.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }

        return Const.AlipayCallback.RESPONSE_FAILED;
    }


    @RequestMapping(value = "query_order_pay_status.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询支付宝状态")
    @ApiImplicitParam(value = "订单号",name = "orderNo",paramType = "query")
    public ServerResponse<Boolean> queryOrderPayStatus(@ApiIgnore HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }

        return iOrderService.queryOrderPayStatus(user.getId(),orderNo);
    }

}
