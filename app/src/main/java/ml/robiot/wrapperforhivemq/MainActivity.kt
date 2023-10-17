package ml.robiot.wrapperforhivemq

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ml.robiot.wrapperforhivemq.ui.theme.WrapperForHivemqTheme
import ml.robiot.wrapperhivemqlibrary.mqttConnect
import ml.robiot.wrapperhivemqlibrary.mqttPublish
import ml.robiot.wrapperhivemqlibrary.mqttSubscribe
import ml.robiot.wrapperhivemqlibrary.newMqtt5SslAsyncClient
import ml.robiot.wrapperhivemqlibrary.trustManagerFactory


class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text: MutableLiveData<String> = MutableLiveData("")
        setContent {
            var text2 by remember {
                mutableStateOf("")
            }
            text.observe(this) {
                text2 = it
            }
            WrapperForHivemqTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(name = text2)
                }
            }
        }
        val am = this@MainActivity.assets
        /*val keyManagerFactory = keyManagerFactory(
            crtFile = am.open("client.crt"),
            keyFile = am.open("client.key"),
            password = "12345"
        )*/
        val trustManagerFactory = trustManagerFactory(caCrtFile = am.open("ca.crt"))
        val client = newMqtt5SslAsyncClient( //newMqtt5AsyncClient("192.168.1.128")/*
            host = "robiot.com.mx",
            trustManagerFactory = trustManagerFactory
        )//("0.tcp.us-cal-1.ngrok.io", 19640)*/

        Log.d(TAG, "onCreate: $client")

        lifecycleScope.launch {
            Log.d(TAG, "onCreate: $client")
            mqttConnect(client, "netuser", "netpassword").collect { mqtt5ConnAck ->
                Log.d(TAG, "onCreate: ${mqtt5ConnAck.reasonCode}")
                mqttSubscribe(client, "questions") { mqtt5Publish ->
                    Log.d(TAG, "onCreate: ${String(mqtt5Publish.payloadAsBytes)}")
                    text.postValue("${text.value}\n${String(mqtt5Publish.payloadAsBytes)}")
                }.collect { mqtt5SubAck ->
                    Log.d(TAG, "onCreate: ${mqtt5SubAck.reasonCodes}")
                    mqttPublish(client, "questions", "Hello from android").collect {
                        Toast.makeText(this@MainActivity, it.publish.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WrapperForHivemqTheme {
        Greeting("Android")
    }
}