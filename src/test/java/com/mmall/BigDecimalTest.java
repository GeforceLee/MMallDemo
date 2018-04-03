package com.mmall;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author geforce
 * @date 2018/4/3
 */

public class BigDecimalTest {

    @Test
    public void test1(){
        BigDecimal b1 = new BigDecimal(0.05);
        BigDecimal b2 = new BigDecimal(0.01);

        System.out.println(b1.add(b1));
    }

    @Test
    public void test2() {
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.01");
        System.out.println(b1.add(b2));
    }
}
