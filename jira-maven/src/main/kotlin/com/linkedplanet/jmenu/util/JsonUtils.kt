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

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.linkedplanet.kotlininsightclient.api.impl.GsonUtil
import java.lang.reflect.InvocationTargetException
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

private class NullableTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType.declaredAnnotations.none { it.annotationClass.qualifiedName == "kotlin.Metadata" }) {
            return null // this adapter is ignored for pure java classes
        }
        val delegate = gson.getDelegateAdapter(this, type)
        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T?) = delegate.write(out, value) // pass through

            override fun read(input: JsonReader): T? {
                val kObject: T = delegate.read(input) ?: return null
                val kotlinClass: KClass<T> = findKotlinClass(type.rawType)
                // Ensure none of its non-nullable fields were deserialized to null
                for (property in kotlinClass.memberProperties) {
                    if (property.returnType.isMarkedNullable) continue
                    try {
                        if (property.get(kObject) == null &&
                            property.name != "type"
                        ) // type was used to determine the class, so this is fine
                            throw jsonParseException(property)
                    } catch (e: InvocationTargetException) {
                        throw jsonParseException(property)
                    }
                }
                return kObject
            }
        }
    }

    private fun <T : Any> jsonParseException(property: KProperty1<T, *>) =
        JsonParseException("Attribute [${property.name}] is required but was not found or was null.")

    private fun <T : Any> findKotlinClass(rawType: Class<in T>): KClass<T> = try {
        @Suppress("UNCHECKED_CAST")
        Class.forName(rawType.name).kotlin as KClass<T>
    } catch (e: ClassNotFoundException) {
        throw JsonParseException("Class $rawType not found", e)
    }
}

private val zonedDateTimeAdapter = object : TypeAdapter<ZonedDateTime>() {
    override fun write(output: JsonWriter, value: ZonedDateTime?) {
        output.value(value?.toOffsetDateTime().toString())
    }

    override fun read(input: JsonReader): ZonedDateTime {
        val dateTimeStr = input.nextString()
        return try {
            ZonedDateTime.parse(dateTimeStr)
        } catch (e: DateTimeParseException) {
            // Normalize and retry if format is non-standard
            if (dateTimeStr.matches(Regex(".+[+-]\\d{4}$"))) {
                // Add the colon in the timezone offset
                val normalized = dateTimeStr.substring(
                    0,
                    dateTimeStr.length - 2
                ) + ":" + dateTimeStr.substring(dateTimeStr.length - 2)
                return ZonedDateTime.parse(normalized)
            }
            throw e
        }
    }
}

val GSON: Gson = GsonUtil
    .gsonBuilder()
    .registerTypeAdapterFactory(NullableTypeAdapterFactory())
    .registerTypeAdapter(ZonedDateTime::class.java, zonedDateTimeAdapter)
    .create()
