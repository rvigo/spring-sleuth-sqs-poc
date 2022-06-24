package com.rvigo.sleuthSqs.controllers

import brave.Tracer
import com.rvigo.sleuthSqs.models.SNSEvent
import com.rvigo.sleuthSqs.publishers.SNSPublisher
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DefaultController(private val publisher: SNSPublisher, private val tracer: Tracer) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping
    fun sendMessage() {
        //get trace and span ids from current context
        val traceId = tracer.currentSpan().context().traceIdString()
        val spanId = tracer.currentSpan().context().spanIdString()

        publisher.publish(
            SNSEvent(
                body = SNSEvent.Body("test"),
                topic = "sns-events-topic",
                attributes = mapOf(
                    "eventType" to "EVENT",
                    "spanId" to spanId,
                    "traceId" to traceId
                )
            )
        )
    }
}