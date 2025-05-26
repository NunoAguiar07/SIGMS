package isel.leic.group25.websockets.hardware.event

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass


@Serializable(with = Event.Companion.EventSerializer::class)
data class Event (
    val type: String,
    @Polymorphic val data: EventData
) {
    companion object {
        val eventModule = SerializersModule {
            polymorphic(EventData::class) {
                subclass(Hello::class)
                subclass(UpdateCapacity::class)
                subclass(Room::class)
                subclass(ReceiveCapacity::class)
            }
        }

        val eventJson = Json {
            serializersModule = eventModule
            classDiscriminator = "type" // this appears inside `data`
            prettyPrint = true
        }

        object EventSerializer : KSerializer<Event> {
            override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Event") {
                element<String>("type")
                element<JsonElement>("data")
            }

            override fun deserialize(decoder: Decoder): Event {
                val input = decoder as? JsonDecoder
                    ?: error("Only JSON supported")

                val jsonObject = input.decodeJsonElement().jsonObject

                val type = jsonObject["type"]?.jsonPrimitive?.content
                    ?: error("Missing 'type' field")

                val dataElement = jsonObject["data"]
                    ?: error("Missing 'data' field")

                val dataSerializer = when (type) {
                    "hello" -> Hello.serializer()
                    "receiveCapacity" -> ReceiveCapacity.serializer()
                    "updateCapacity" -> UpdateCapacity.serializer()
                    "room" -> Room.serializer()
                    else -> error("Unknown type: $type")
                }

                val data = input.json.decodeFromJsonElement(dataSerializer, dataElement)
                return Event(type, data)
            }

            override fun serialize(encoder: Encoder, value: Event) {
                val output = encoder as? JsonEncoder
                    ?: error("Only JSON supported")

                val dataElement = when (val d = value.data) {
                    is Hello -> output.json.encodeToJsonElement(Hello.serializer(), d)
                    is ReceiveCapacity -> output.json.encodeToJsonElement(ReceiveCapacity.serializer(), d)
                    is Room -> output.json.encodeToJsonElement(Room.serializer(), d)
                    is UpdateCapacity -> output.json.encodeToJsonElement(UpdateCapacity.serializer(), d)
                }

                val json = buildJsonObject {
                    put("type", JsonPrimitive(value.type))
                    put("data", dataElement)
                }

                output.encodeJsonElement(json)
            }
        }
    }


}

