package com.cricFizzAlerts.consumer;

import com.cricFizzAlerts.bean.alert.AlertDetails;
import com.cricFizzAlerts.bean.matchScoreCard.MatchScoreCard;
import com.cricFizzAlerts.repository.AlertsRepository;
import com.cricFizzAlerts.services.CricbuzzService;
import com.cricFizzAlerts.services.SendAlertsService;
import com.cricFizzAlerts.utils.CricAlertUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CricfizzAlertsKafkaConsumer {

    private final Logger logger = LoggerFactory.getLogger(CricfizzAlertsKafkaConsumer.class);

    @Autowired
    private CricAlertUtils cricAlertUtils;

    @Autowired
    private AlertsRepository alertsRepository;

    @Autowired
    private SendAlertsService sendAlertsService;

    @KafkaListener(topics = "${spring.kafka.topic-name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAlertDetails(String alertDetailsJson){
        try {
            AlertDetails alertDetails = cricAlertUtils.objectMapper().readValue(alertDetailsJson, AlertDetails.class);
            logger.info("Alert Received: {}",alertDetails.getAlertId());
            alertsRepository.save(alertDetails);
            logger.info("Alert: {} saved",alertDetails.getAlertId());
            sendAlertsService.scheduleMailAlerts(alertDetails);
        } catch (JsonProcessingException e) {
            logger.error("Exception in parsing alertDetailsJson: {}",e.getMessage());
        }
    }
}
