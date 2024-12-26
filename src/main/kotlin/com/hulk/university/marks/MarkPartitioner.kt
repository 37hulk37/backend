package com.hulk.university.marks

import org.apache.kafka.clients.producer.Partitioner
import org.apache.kafka.common.Cluster
import kotlin.math.abs

class MarkPartitioner: Partitioner {

    override fun partition(topic: String, key: Any?, keyBytes: ByteArray?, value: Any?, valueBytes: ByteArray, cluster: Cluster): Int {
        val partitionsNumber = cluster.partitionCountForTopic(topic)
        val studentId = key as Long

        return abs(studentId.toInt()) % partitionsNumber
    }

    override fun configure(configs: MutableMap<String, *>?) {}

    override fun close() {}
}