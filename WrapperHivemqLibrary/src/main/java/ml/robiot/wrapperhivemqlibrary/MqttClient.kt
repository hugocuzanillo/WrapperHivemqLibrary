package ml.robiot.wrapperhivemqlibrary

import com.hivemq.client.mqtt.MqttClient
import java.util.UUID
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory

fun newMqtt5AsyncClient(
    host: String,
    port: Int = MqttClient.DEFAULT_SERVER_PORT,
    identifier: String = UUID.randomUUID().toString()) =
    MqttClient.builder()
        .identifier(identifier)
        .serverHost(host)
        .serverPort(port)
        .automaticReconnectWithDefaultConfig()
        .useMqttVersion5()
        .buildAsync()

fun newMqtt5SslAsyncClient(
    host: String,
    port: Int = MqttClient.DEFAULT_SERVER_PORT_SSL,
    identifier: String = UUID.randomUUID().toString(),
    keyManagerFactory: KeyManagerFactory? = null,
    trustManagerFactory: TrustManagerFactory,
    protocols: List<String> = listOf("TLSv1.2", "TLSv1.3"),
) =
    MqttClient.builder()
        .identifier(identifier)
        .serverHost(host)
        .serverPort(port)
        .sslConfig()
        .keyManagerFactory(keyManagerFactory)
        .trustManagerFactory(trustManagerFactory)
        .protocols(protocols)
        .applySslConfig()
        .automaticReconnectWithDefaultConfig()
        .useMqttVersion5()
        .buildAsync()