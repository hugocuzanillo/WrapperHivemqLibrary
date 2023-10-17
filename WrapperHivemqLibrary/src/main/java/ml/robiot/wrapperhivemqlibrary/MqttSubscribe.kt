package ml.robiot.wrapperhivemqlibrary

import android.util.Log
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.callbackFlow

fun mqttSubscribe(client: Mqtt5AsyncClient, topic: String, qos: MqttQos = MqttQos.EXACTLY_ONCE, callback: (Mqtt5Publish) -> Unit) = callbackFlow {
    val tag = "MqttSubscribe"
    client.subscribeWith()
        .topicFilter(topic)
        .qos(qos)
        .callback {
            callback(it)
        }
        .send()
        .whenComplete { subAck, throwable ->
        if (throwable != null) {
            Log.d(tag, "Throwable message: ${throwable.message}")
        } else {
            trySend(subAck).onFailure {
                Log.d(tag, "Throwable message: ${it?.message}")
            }
        }
    }
    awaitClose { client.unsubscribeWith().topicFilter(topic).send().isDone }
}