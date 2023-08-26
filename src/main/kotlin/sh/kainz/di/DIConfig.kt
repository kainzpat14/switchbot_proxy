package sh.kainz.di

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty
import sh.kainz.switchbotApi.ISwitchbotApi
import sh.kainz.switchbotApi.ISwitchbotSigner
import sh.kainz.switchbotApi.SwitchbotApi
import sh.kainz.switchbotApi.SwitchbotSigner

class DIConfig {
    @Produces
    @ApplicationScoped
    fun createSwitchbotSigner(@ConfigProperty(name = "switchbot.token") token : String, @ConfigProperty(name = "switchbot.secret") secret : String) : ISwitchbotSigner{
        return SwitchbotSigner(token, secret)
    }

    @Produces
    @ApplicationScoped
    fun createSwitchbotApi(signer : ISwitchbotSigner) : ISwitchbotApi {
        return SwitchbotApi(signer)
    }
}