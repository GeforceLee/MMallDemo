package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

/**
 * @author geforce
 * @date 2018/4/4
 */
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse aliCallBack(Map<String ,String > params);

    ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderNo);
}
