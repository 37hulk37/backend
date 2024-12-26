package com.hulk.university.retryabletasks

import com.hulk.university.allOfFutures
import com.hulk.university.enums.RetryableTaskState
import com.hulk.university.exceptions.ApplicationException
import com.hulk.university.retryabletasks.producers.RetryableTaskPayloadProducer
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class RetryableTaskScheduler(
    private val retryableTaskService: RetryableTaskService,
    private val producers: List<RetryableTaskPayloadProducer>,
) {

    private val producersByType by lazy {
        producers.associateBy { it.getType() }
    }
    private val log = LoggerFactory.getLogger(RetryableTaskScheduler::class.java)!!

    @Scheduled(cron = "\${app.scheduler.cron.interval}")
    fun scheduledSending(): CompletableFuture<Void> {
        val tasks = retryableTaskService.getTasks()
        if (tasks.isEmpty()) {
            log.debug("There are no tasks in the queue")
            return CompletableFuture.completedFuture(null)
        }

        return allOfFutures(tasks.map { task ->
            val producer = getProducerByTypeOrElseThrow(task.type!!)
            producer.send(task)
        })
            .thenAccept { _ -> retryableTaskService.updateTasks(tasks) {
                task -> task.state = RetryableTaskState.Completed
            } }
    }

    private fun getProducerByTypeOrElseThrow(type: String): RetryableTaskPayloadProducer =
        producersByType[type] ?:
            throw ApplicationException("There is no producer for task $type")

}