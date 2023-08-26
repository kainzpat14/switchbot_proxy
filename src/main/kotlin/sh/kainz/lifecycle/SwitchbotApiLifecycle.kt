package sh.kainz.lifecycle

import io.quarkus.arc.impl.UncaughtExceptions.LOGGER
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import sh.kainz.switchbotApi.ISwitchbotApi


@ApplicationScoped
class SwitchbotApiLifecycle {
    @ConfigProperty(name = "switchbot.webhook.url")
    lateinit var webhookUrl: String

    @Inject
    lateinit var switchbotApi : ISwitchbotApi

    fun onStart(@Observes ev: StartupEvent?) {
        switchbotApi.registerWebhook(webhookUrl);
    }


}