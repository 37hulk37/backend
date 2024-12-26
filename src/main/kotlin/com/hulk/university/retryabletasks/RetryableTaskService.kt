package com.hulk.university.retryabletasks

import com.fasterxml.jackson.databind.ObjectMapper
import com.hulk.university.create
import com.hulk.university.enums.RetryableTaskState
import com.hulk.university.tables.daos.RetryableTaskDao
import com.hulk.university.tables.pojos.RetryableTask
import com.hulk.university.tables.references.RETRYABLE_TASK
import com.hulk.university.updateAll
import org.jooq.JSONB
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class RetryableTaskService(
    private val retryableTaskDao: RetryableTaskDao,
    private val objectMapper: ObjectMapper,
) {

    @Transactional
    fun createTask(value: Any, payloadType: String): RetryableTask {
        val payload = JSONB.jsonb(objectMapper.writeValueAsString(value))

        return retryableTaskDao.create(RetryableTask(
            payload = payload,
            type = payloadType,
            state = RetryableTaskState.Created,
            retryAt = Instant.now()
        ))
    }

    @Transactional
    fun getTasks(): List<RetryableTask> {
        val now = Instant.now()
        val tasks = getTasksByRetryAt(now)

        return updateTasks(tasks) { task ->
            task.retryAt = now.plus(10, ChronoUnit.MINUTES)
            task.state = RetryableTaskState.InProgress
        }
    }

    @Transactional
    fun updateTasks(tasks: List<RetryableTask>, consumer: (RetryableTask) -> Unit): List<RetryableTask> {
        tasks.forEach { task -> consumer(task) }

        return retryableTaskDao.updateAll(tasks)
    }

    private fun getTasksByRetryAt(retryAt: Instant) = retryableTaskDao.ctx()
        .selectFrom(RETRYABLE_TASK)
        .where(RETRYABLE_TASK.STATE.eq(RetryableTaskState.Created)
            .and(RETRYABLE_TASK.RETRY_AT.le(retryAt))
        )
        .fetchInto(RetryableTask::class.java)

}