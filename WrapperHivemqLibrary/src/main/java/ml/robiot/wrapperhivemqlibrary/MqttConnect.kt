package ml.robiot.wrapperhivemqlibrary

import android.util.Log
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

fun mqttConnect(client: Mqtt5AsyncClient, username: String, password: String) = callbackFlow {
    val tag = "MqttConnect"
    val completableFuture = client
        .connectWith()
        .simpleAuth()
        .username(username)
        .password(password.encodeToByteArray())//.toByteArray(UTF_8))
        .applySimpleAuth()
        .send()
        .whenComplete { mqtt5ConnAck, throwable ->
            if (throwable != null) {
                cancel(tag, throwable.cause)
            } else {
                Log.d(tag, "mqttConnect: Successfully")
                trySendBlocking(mqtt5ConnAck).onFailure {
                    Log.d(tag, "Throwable $it")
                }
            }
        }

    awaitClose { completableFuture.isDone }
}.flowOn(Dispatchers.IO)