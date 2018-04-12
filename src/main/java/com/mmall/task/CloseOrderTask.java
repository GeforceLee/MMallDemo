package com.mmall.task;

import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author geforce
 * @date 2018/4/12
 */
@Component
@Slf4j
@EnableScheduling
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    //每一分钟执行一次
    @Scheduled(cron="0 */1 * * * ?")
    public void closeOrderTaskV1() {
        log.info("关闭订单定任务启动");

        int hour = Integer.parseInt(PropertiesUtil.getProperty("colse.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        log.info("关闭订单定任务完成");
    }
}
