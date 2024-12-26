package com.hulk.university.retryabletasks.producers

import java.util.concurrent.CompletableFuture

interface RetryableTaskPayloadProducer {

    fun getType(): String

    fun send(payload: Any): CompletableFuture<Unit>

}