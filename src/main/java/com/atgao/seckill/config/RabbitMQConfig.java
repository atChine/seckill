package com.atgao.seckill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：atGaoJu
 * @date ：Created in 2022/3/28 12:01 PM
 * @GetHub：https://github.com/atChine
 * @Blog：https://blog.csdn.net/qq_43649569?spm=1000.2115.3001.5343
 */
@Configuration
public class RabbitMQConfig {
   //初始化队列
    @Bean
    public Queue queue(){
        return new Queue("queue",true);
    }
}
