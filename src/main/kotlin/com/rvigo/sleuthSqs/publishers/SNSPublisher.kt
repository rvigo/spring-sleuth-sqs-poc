package com.rvigo.sleuthSqs.publishers

import com.fasterxml.jackson.databind.ObjectMapper
import com.rvigo.sleuthSqs.models.SNSEvent
import org.slf4j.LoggerFactory
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.stereotype.Component

@Component
class SNSPublisher(private val messagingTemplate: NotificationMessagingTemplate) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val mapper = ObjectMapper()

    fun publish(event: SNSEvent) {
        val body = mapper.writeValueAsString(event.body)
        logger.info("sending message: $body with attributes: ${event.attributes} ")
        messagingTemplate.convertAndSend(event.topic, body, event.attributes)
    }
}