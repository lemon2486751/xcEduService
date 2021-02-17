package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.manage_cms_client.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author lemon
 * @date 2021/2/17 14:20
 */
@Component
public class CustomerPostPage {
    @Autowired
    private PageService pageService;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg) {
        //解析消息体
        Map map = JSON.parseObject(msg, Map.class);
        //获取pageId
        String pageId = (String) map.get("pageId");
        //将页面存储到本地
        pageService.saveFileToServerPath(pageId);
    }
}
