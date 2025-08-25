package com.linkedplanet.com.service.orderprocessing

import arrow.core.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.io.File
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() =
        testApplication {
            application {
                module()
            }

            client.get("/health").apply {
                assertEquals(HttpStatusCode.OK, status)
            }
        }
}

/*
 * Copyright 2025 linked-planet GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
