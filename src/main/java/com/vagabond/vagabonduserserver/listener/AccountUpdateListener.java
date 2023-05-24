package com.vagabond.vagabonduserserver.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class AccountUpdateListener {
    @RabbitListener(queues = "${rabbit.queue.user}")
    public void listen() {
    }
}
