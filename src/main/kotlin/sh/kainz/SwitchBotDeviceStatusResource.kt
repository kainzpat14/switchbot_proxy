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


@Path("/deviceStatusCollector")
class SwitchBotDeviceStatusResource {

    @Inject
    lateinit var deviceStatusService: ISwitchbotDeviceStatusService
    @POST
    fun collectStatus(body : JsonObject) {
        if(body.containsKey("eventType") && (body["eventType"] as JsonString).string == "changeReport") {
            val context = body["context"] as JsonObject;
            println(context);
            deviceStatusService.trackDeviceStatus(context.getString("deviceMac"),context)
        }
    }

}