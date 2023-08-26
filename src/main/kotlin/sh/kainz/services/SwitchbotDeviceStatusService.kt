package sh.kainz.services

import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.json.JsonObject
import sh.kainz.switchbotApi.ISwitchbotApi
import sh.kainz.switchbotApi.SwitchbotDevice

interface ISwitchbotDeviceStatusService {
    fun listDevices() : List<SwitchbotDevice>

    fun trackDeviceStatus(deviceId : String, status : JsonObject)

    fun getDeviceStatus(deviceId : String) : JsonObject
}

@ApplicationScoped
class SwitchbotDeviceStatusService : ISwitchbotDeviceStatusService{
    lateinit var devices : List<SwitchbotDevice>

    @Inject
    lateinit var switchbotApi : ISwitchbotApi

    val deviceStatus = HashMap<String, JsonObject>()

    @PostConstruct
    fun init() {
        devices = switchbotApi.listDevices()
    }

    override fun listDevices(): List<SwitchbotDevice> {
        return devices
    }

    override fun trackDeviceStatus(deviceId: String, status: JsonObject) {
        deviceStatus[deviceId]=status
    }

    override fun getDeviceStatus(deviceId: String): JsonObject {
        return deviceStatus.computeIfAbsent(deviceId) {
            println("loading device status for deviceId $deviceId")
            switchbotApi.getDeviceStatus(deviceId)
        }
    }


}