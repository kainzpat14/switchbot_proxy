package sh.kainz

import jakarta.inject.Inject
import jakarta.json.JsonObject
import jakarta.json.JsonString
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import sh.kainz.services.ISwitchbotDeviceStatusService
import sh.kainz.switchbotApi.SwitchbotDevice

@Path("/")
class SwitchbotResource {
    @Inject
    lateinit var deviceStatusService: ISwitchbotDeviceStatusService

    @GET
    @Path("/{deviceId}")
    fun getDeviceStatus(@PathParam("deviceId") deviceId : String) : JsonObject {
        return deviceStatusService.getDeviceStatus(deviceId);
    }

    @GET
    @Path("/devices")
    fun getDevices() : List<SwitchbotDevice> {
        return deviceStatusService.listDevices()
    }
}