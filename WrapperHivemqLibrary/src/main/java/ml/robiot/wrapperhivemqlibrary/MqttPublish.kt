package ml.robiot.wrapperhivemqlibrary

import android.util.Log
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.callbackFlow

fun mqttPublish(client: Mqtt5AsyncClient, topic: String, payload: String, qos: MqttQos = MqttQos.EXACTLY_ONCE, retain: Boolean = true) = callbackFlow {
    val tag = "mqttPublish"
    val completableFuture = client.publishWith()
        .topic(topic)
        .payload(payload.encodeToByteArray())
        .qos(qos)
        .retain(retain)
        .send()
        .whenComplete { publishResult, throwable  ->
        if (throwable != null) {
            Log.d(tag, "Throwable message: ${throwable.message}")
        } else {
            trySend(publishResult).onFailure {
                Log.d(tag, "Throwable message: ${it?.message}")
            }
        }
    }

    awaitClose { completableFuture.isDone }
}