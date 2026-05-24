package com.example.permission.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EXCHANGE = "user_exchange";
    public static final String USER_CREATE_QUEUE = "user_create_queue";
    public static final String USER_UPDATE_QUEUE = "user_update_queue";
    public static final String USER_DELETE_QUEUE = "user_delete_queue";
    public static final String LOG_EXCHANGE = "log_exchange";
    public static final String LOG_QUEUE = "log_queue";

    @Bean
    public Exchange userExchange() {
        return ExchangeBuilder.directExchange(USER_EXCHANGE).durable(true).build();
    }

    @Bean
    public Exchange logExchange() {
        return ExchangeBuilder.directExchange(LOG_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue userCreateQueue() {
        return QueueBuilder.durable(USER_CREATE_QUEUE).build();
    }

    @Bean
    public Queue userUpdateQueue() {
        return QueueBuilder.durable(USER_UPDATE_QUEUE).build();
    }

    @Bean
    public Queue userDeleteQueue() {
        return QueueBuilder.durable(USER_DELETE_QUEUE).build();
    }

    @Bean
    public Queue logQueue() {
        return QueueBuilder.durable(LOG_QUEUE).build();
    }

    @Bean
    public Binding userCreateBinding() {
        return BindingBuilder.bind(userCreateQueue()).to(userExchange()).with("user.create").noargs();
    }

    @Bean
    public Binding userUpdateBinding() {
        return BindingBuilder.bind(userUpdateQueue()).to(userExchange()).with("user.update").noargs();
    }

    @Bean
    public Binding userDeleteBinding() {
        return BindingBuilder.bind(userDeleteQueue()).to(userExchange()).with("user.delete").noargs();
    }

    @Bean
    public Binding logBinding() {
        return BindingBuilder.bind(logQueue()).to(logExchange()).with("log.create").noargs();
    }
}