package com.rvigo.sleuthSqs.configs

import brave.Tracing
import brave.instrumentation.aws.sqs.SqsMessageTracing
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary


@Configuration
@EnableSqs
class SQSConfiguration(
    @Value("\${aws.region}")
    private val awsRegion: String,
    @Value("\${aws.sqs.endpoint}")
    private val endpoint: String,
) {

    @Bean
    fun queueMessagingTemplate(
        SQSAsync: AmazonSQSAsync
    ): QueueMessagingTemplate =
        QueueMessagingTemplate(SQSAsync)


    @Bean
    @Primary
    fun amazonSqsAsync(): AmazonSQSAsync = createSQSAsync(endpoint, awsRegion)

    private fun createSQSAsync(
        endpoint: String,
        region: String
    ): AmazonSQSAsync {

        val currentTracing = Tracing.current()
        val sqsMessageTracing = SqsMessageTracing.create(currentTracing)

        return AmazonSQSAsyncClientBuilder.standard()
            .withRequestHandlers(sqsMessageTracing.requestHandler()) //does nothing :/
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .build()
    }
}