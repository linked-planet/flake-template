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
package it

import com.google.gson.JsonParser
import com.linkedplanet.jmenu.util.JiraIssueUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.File

class MenuIntegrationTest {

    @ParameterizedTest
    @CsvSource("admin,admin,menu-entries-admin.json", "test1,1234,menu-entries-user.json")
    fun `check service menu entries as regular user with hidden entries`(
        username: String,
        password: String,
        expectedFilePath: String
    ) {
        val expectedFile = this::class.java.classLoader.getResource(expectedFilePath)
            ?: fail("File '${expectedFilePath}' not found.")
        val expectedJson = File(expectedFile.path).readText()
        val expectedObject = JsonParser().parse(expectedJson)

        val curJson = JiraIssueUtils.getServiceMenuEntriesJson(username, password)
        val curObject = JsonParser().parse(curJson)

        assertEquals(expectedObject, curObject)
    }
}
