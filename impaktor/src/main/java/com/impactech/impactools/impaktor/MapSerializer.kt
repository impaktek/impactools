package com.impactech.impactools.impaktor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable(with = MapWrapperSerializer::class)
data class MapWrapper(val data: Map<String, Any>)

object MapWrapperSerializer : KSerializer<MapWrapper> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MapWrapper") {
        mapSerialDescriptor(String.serializer().descriptor, String.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: MapWrapper) {
        val mapEncoder = encoder.beginStructure(descriptor)
        value.data.forEach { (key, entryValue) ->
            mapEncoder.encodeStringElement(descriptor, 0, key)
            mapEncoder.encodeStringElement(descriptor, 1, entryValue.toString())
        }
        mapEncoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): MapWrapper {
        val map = mutableMapOf<String, Any>()
        val mapDecoder = decoder.beginStructure(descriptor)
        while (true) {
            val index = mapDecoder.decodeElementIndex(descriptor)
            if (index == CompositeDecoder.DECODE_DONE) break
            val key = mapDecoder.decodeStringElement(descriptor, 0)
            val value = mapDecoder.decodeStringElement(descriptor, 1)
            map[key] = value
        }
        mapDecoder.endStructure(descriptor)
        return MapWrapper(map)
    }
}
