package com.rvigo.sleuthSqs.listeners

import org.slf4j.LoggerFactory
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class SQSListener {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @SqsListener("\${aws.sqs.queues.main-queue}", deletionPolicy = ON_SUCCESS)
    fun receiver(@Payload message: String, @Headers headers: Map<String, Any>) {
        val traceId = headers["traceId"]?.toString()
        val spanId = headers["spanId"]?.toString()
        logger.info("received message: $message with traceId: $traceId and spanId: $spanId")
    }
}