package com.cricFizzAlerts.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CricfizzAlertsKafkaConsumer {

    private final Logger logger = LoggerFactory.getLogger(CricfizzAlertsKafkaConsumer.class);

    @KafkaListener(topics = "${spring.kafka.topic-name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAlertDetails(String alertDetailsJson){
        logger.info(alertDetailsJson);
    }
}
