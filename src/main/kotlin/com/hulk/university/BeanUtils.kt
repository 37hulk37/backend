package com.hulk.university

import java.util.concurrent.CompletableFuture

fun <T> setIfNotNull(value: T, consumer: (T) -> Unit) {
    value?.let { consumer(it) }
}

fun <T> allOfFutures(futures: List<CompletableFuture<T>>): CompletableFuture<List<T>> =
    CompletableFuture.allOf(*futures.toTypedArray())
        .thenApply {
            futures.map { future -> future.join() }
        }