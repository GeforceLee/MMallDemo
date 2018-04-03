package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

/**
 * @author geforce
 * @date 2018/4/3
 */
public interface IProductService {

    ServerResponse saveOrUpdateUpdate(Product product);

    ServerResponse<String> setSaleStatus(Integer productId,Integer status);

    ServerResponse<Object> manageProductDetail(Integer productId);
}
