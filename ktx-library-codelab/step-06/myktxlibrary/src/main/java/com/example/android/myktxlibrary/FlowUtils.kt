package com.example.android.myktxlibrary

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Catches exceptions in the flow completion and calls a specified [action] with
 * the caught exception to convert which into a normal value to downstream.
 *
 * For example,
 * ```
 * flow {
 *     emit("1")
 *     throw Exception("error")
 *     emit("2") // this emission is unreachable
 * }
 *     .rescue { throwable -> throwable.message } // catches error and emit it as an value
 *     .collect { println(it) } // this will print "1", "error"
 * ```
 */
fun <T> Flow<T>.rescue(action: suspend (cause: Throwable) -> T): Flow<T> =
    catch { emit(action(it)) }

/**
 * Convert flows to encapsulates an optional value.
 * THis returns a flow which values are wrapped by [Result.success] if succeed while
 * [Result.failure] if failed.
 */
fun <T> Flow<T>.wrapByResult(): Flow<Result<T>> =
    this
        .map { Result.success(it) }
        .rescue { Result.failure(it) }