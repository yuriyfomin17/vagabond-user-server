package com.vagabond.vagabonduserserver.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {
    @Value("${rabbit.exchange.name}")
    private String exchangeName;

    @Value("${rabbit.queue.login}")
    private String loginQueueName;
    @Value("${rabbit.key.login}")
    private String loginRoutingKey;

    @Bean
    public Binding loginBinding() {
        return BindingBuilder
                .bind(loginQueue())
                .to(exchange())
                .with(loginRoutingKey).noargs();
    }

    @Bean
    public Queue loginQueue() {
        return QueueBuilder
                .durable(loginQueueName)
                .build();
    }

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder
                .directExchange(exchangeName)
                .build();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}