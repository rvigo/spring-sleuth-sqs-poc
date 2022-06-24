package com.rvigo.sleuthSqs.configs

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.config.annotation.EnableSns
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
@EnableSns
class SNSConfiguration(
    @Value("\${aws.region}") private val awsRegion: String,
    @Value("\${aws.sns.endpoint}") private val endpoint: String
) {

    @Bean
    fun notificationMessagingTemplate(SNSAsync: AmazonSNSAsync): NotificationMessagingTemplate =
        NotificationMessagingTemplate(SNSAsync)

    @Bean
    @Primary
    fun amazonSnsAsync(): AmazonSNSAsync = createSNSAsync(endpoint, awsRegion)

    private fun createSNSAsync(endpoint: String, region: String): AmazonSNSAsync =
        AmazonSNSAsyncClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .build()
}