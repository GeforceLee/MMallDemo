package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param("productName")String productName,@Param("prodcutId")Integer prodcutId);

    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName,@Param("categoryIds")List<Integer> categoryIds);

    //这里一定要用Intgere 有可能差不到商品,返回为null
    Integer selectStockByProductId(Integer id);
}