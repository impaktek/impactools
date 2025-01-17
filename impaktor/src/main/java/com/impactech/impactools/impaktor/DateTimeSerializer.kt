package com.impactech.impactools.impaktor

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object DateTimeSerializer: KSerializer<kotlinx.datetime.LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): kotlinx.datetime.LocalDateTime {
        val dateString = decoder.decodeString()
        return if (dateString.endsWith("Z")) {
            // Parse as UTC and convert to LocalDateTime
            Instant.parse(dateString).toLocalDateTime(TimeZone.currentSystemDefault())
        } else {
            // Handle cases without 'Z'
            kotlinx.datetime.LocalDateTime.parse(dateString)
        }
    }

    override fun serialize(encoder: Encoder, value: kotlinx.datetime.LocalDateTime) {
        val instant = value.toInstant(TimeZone.currentSystemDefault())
        encoder.encodeString(instant.toString())
    }
}

object DateSerializer: KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDate {
        val dateString = decoder.decodeString()
        return LocalDate.parse(dateString)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        val dateString = value.toString()
        encoder.encodeString(dateString)
    }
}

object TimeSerializer: KSerializer<LocalTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalTime {
        val dateString = decoder.decodeString()
        return LocalTime.parse(dateString)
    }

    override fun serialize(encoder: Encoder, value: LocalTime) {
        val dateString = value.toString()
        encoder.encodeString(dateString)
    }
}