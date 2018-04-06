package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * @author geforce
 * @date 2018/4/4
 */
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse aliCallBack(Map<String ,String > params);

    ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderNo);

    ServerResponse<OrderVo> cratetOrder(Integer userId, Integer shippingId);

    ServerResponse<String> canecel(Integer userId,Long orderNo);
}
