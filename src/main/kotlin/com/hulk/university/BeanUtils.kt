package com.hulk.university

fun <T> setIfNotNull(value: T, consumer: (T) -> Unit) {
    value?.let { consumer(it) }
}