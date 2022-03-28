package com.atgao.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @author ：atGaoJu
 * @date ：Created in 2022/3/28 12:08 PM
 * @GetHub：https://github.com/atChine
 * @Blog：https://blog.csdn.net/qq_43649569?spm=1000.2115.3001.5343
 */
@Service
@Slf4j
public class MQreceiver {

    @RabbitListener(queues = "queue")
    public void receive(Object msg){
        log.info("接收到消息："+ msg);
    }
}
