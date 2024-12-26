package com.hulk.university.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@EnableConfigurationProperties(KafkaProperties::class)
@Configuration
class KafkaConfiguration {

    @Bean
    fun marksTopic(): NewTopic = TopicBuilder
        .name("marks")
        .build()

}