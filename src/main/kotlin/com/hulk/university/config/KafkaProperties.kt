package com.hulk.university.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.kafka")
data class KafkaProperties(
    val markTopicName: String,
)