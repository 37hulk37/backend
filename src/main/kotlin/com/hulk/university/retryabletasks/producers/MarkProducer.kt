package com.hulk.university.retryabletasks.producers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.hulk.university.config.KafkaProperties
import com.hulk.university.tables.pojos.Mark
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class MarkProducer(
    private val kafkaTemplate: KafkaTemplate<Long, Mark>,
    private val properties: KafkaProperties,
): RetryableTaskPayloadProducer {

    private val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(SimpleModule()
            .addDeserializer(Mark::class.java, MarkDeserializer())
        )
    private val log = LoggerFactory.getLogger(MarkProducer::class.java)


    override fun getType(): String =
        Mark::class.java.simpleName

    override fun send(payload: Any): CompletableFuture<Unit> {
        val mark = objectMapper.convertValue(payload, Mark::class.java)

        return kafkaTemplate.send(properties.markTopicName, mark.studentId!!.toInt(), mark.id!!, mark)
            .whenComplete { _, t ->
                if (t != null) {
                    log.error("Error sending mark payload: {}", t.message)
                }
            }
            .thenApply {}
    }

    class MarkDeserializer: JsonDeserializer<Mark>() {

        override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Mark {
            val node = jp.readValueAsTree<JsonNode>()

            return Mark(
                id = node.get("id").asLong(),
                studentId = node.get("studentId").asLong(),
                subjectId = node.get("subjectId").asLong(),
                teacherId = node.get("teacherId").asLong(),
                value = node.get("value").asInt(),
                year = node.get("year").asInt()
            )
        }
    }

}