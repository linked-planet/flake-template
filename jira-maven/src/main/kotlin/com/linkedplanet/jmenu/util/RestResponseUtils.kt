/*-
 * #%L
 * jmenu
 * %%
 * Copyright (C) 2025 linked-planet GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.linkedplanet.jmenu.util

import arrow.core.Either
import com.linkedplanet.jmenu.impl.error.DomainError
import com.linkedplanet.jmenu.impl.error.ErrorCode
import com.linkedplanet.kotlininsightclient.api.error.InsightClientError
import javax.ws.rs.core.Response


fun String.toResponse(): Response =
    Response.ok(this).build()

fun <T> T.toResponse(): Response =
    Response.ok(GSON.toJson(this)).build()


// this is more specific, so the compiler picks this over T.toResponse() when the signature matches
fun <ErrorType, T> Either<ErrorType, T>.toResponse(): Response =
    this.handleDomainError()
        .handleJsonResponse()

fun DomainError.toErrorResponse(): Response =
    Response.ok(GSON.toJson(this)).status(this.httpStatusCode ?: 400).build()

fun <T> Either<DomainError, T>.handleJsonResponse(): Response =
    this.handle {
        val json = GSON.toJson(it)
        Response.ok(json).build()
    }

fun Either<DomainError, String>.handleJsonStringResponse(): Response =
    this.handle {
        Response.ok(it).build()
    }

fun Either<DomainError, Unit>.handleOK(): Response =
    this.handle {
        Response.ok().build()
    }

private fun <T> Either<DomainError, T>.handle(function: (T) -> Response): Response =
    this.fold<Response>(
        { error -> return error.toErrorResponse() }
    ) {
        return function(it)
    }

fun InsightClientError.toDomainError(): DomainError =
    DomainError(error, message, ErrorCode.INSIGHT_CLIENT_ERROR, statusCode, stacktrace)

fun <E, T> Either<E, T>.handleDomainError(): Either<DomainError, T> =
    this.mapLeft { error ->
        when (error) {
            is InsightClientError -> error.toDomainError()
            is DomainError -> error
            else -> DomainError("Unknown Either.Left", "Unknown Error found on the left side of Either<E, T>")
        }
    }
