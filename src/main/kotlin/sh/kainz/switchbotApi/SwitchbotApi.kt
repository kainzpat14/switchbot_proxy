package sh.kainz.switchbotApi;

import jakarta.json.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import kotlin.math.sign
import java.io.StringReader
import java.lang.RuntimeException

const val SWITCHBOT_BASE_URL = "https://api.switch-bot.com/v1.1"

data class SwitchbotDevice(val deviceId : String, val deviceName : String, val deviceType : String)

interface ISwitchbotApi {
    fun registerWebhook(webhook : String)

    fun listDevices() : List<SwitchbotDevice>

    fun getDeviceStatus(deviceId : String) : JsonObject
}

class SwitchbotApi(val signer : ISwitchbotSigner) : ISwitchbotApi {

    override fun registerWebhook(webhook: String) {
        val url = findWebhookValue()

        if(url == null || (url as JsonString).string != webhook) {
            if(url != null) {
                updateWebhook(webhook)
            } else {
                setupWebhook(webhook)
            }
        }
    }

    override fun listDevices(): List<SwitchbotDevice> {
        val response = executeRequest("GET","devices")
        val devices = response["body"]?.asJsonObject()?.get("deviceList")?.asJsonArray()?.toList();
        if(devices == null) {
            throw RuntimeException("Unable to get devices, response: $response")
        }
        return devices.map { it as JsonObject }.map { SwitchbotDevice(it.getString("deviceId"),it.getString("deviceName"),it.getString("deviceType")) }
    }

    override fun getDeviceStatus(deviceId: String): JsonObject {
        val response = executeRequest("GET","devices/$deviceId/status")
        val body = response["body"]
                ?: throw RuntimeException("Device status for $deviceId could not be determined, response: $response")
        return body.asJsonObject()
    }

    private fun findWebhookValue(): JsonValue? {
        val response = executeRequest("POST", "webhook/queryWebhook", """
                {
                    "action": "queryUrl"
                }
            """.trimIndent());

        val url = response["body"]?.asJsonObject()?.get("urls")?.asJsonArray()?.get(0);
        return url
    }

    private fun updateWebhook(webhook: String) {
        executeRequest("POST", "webhook/updateWebhook", """
                        {
                            "action": "updateWebhook",
                            "config":{
                                "url": "$webhook",
                                "enable":true
                            }
                        }
                    """.trimIndent())
    }

    private fun setupWebhook(webhook: String) {
        executeRequest("POST", "webhook/setupWebhook", """
                        {
                            "action":"setupWebhook",
                            "url":"$webhook", // enter your url
                            "deviceList":"ALL"
                        }
                    """.trimIndent())
    }

    private fun executeRequest(method : String, url : String, json : String? = null) : JsonObject {
        val signature = signer.sign();
        val bodyPublisher = if(json == null) BodyPublishers.noBody() else BodyPublishers.ofString(json)
        val contentType = if(json == null) "" else "application/json"
        val request = HttpRequest.newBuilder()
                .header("Authorization",signature.token)
                .header("t",signature.time)
                .header("nonce",signature.nonce)
                .header("sign",signature.signature)
                .header("Content-Type",contentType)
                .uri(URI.create("${SWITCHBOT_BASE_URL}/$url"))
                .method(method, bodyPublisher)
                .build()
        val response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())
        if(response.statusCode() > 300) {
            throw RuntimeException("Request failed: "+response.statusCode()+" body: "+response.body())
        }
        val responseBody = response.body();
        return Json.createReader(StringReader(responseBody)).readObject();
    }
}