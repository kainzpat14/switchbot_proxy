package sh.kainz.authentication

import io.quarkus.security.UnauthorizedException
import jakarta.ws.rs.container.ContainerRequestContext
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.resteasy.reactive.server.ServerRequestFilter

class AuthenticationFilter(@ConfigProperty(name = "security.api-keys") private val apiKeys: List<String>) {
    @ServerRequestFilter(preMatching = true)
    fun filterApiKey(containerRequestContext: ContainerRequestContext) {
        val apiKeyHeader = containerRequestContext.getHeaderString("x-api-key")

        if (!containerRequestContext.uriInfo.path.equals("/deviceStatusCollector") && !apiKeys.contains(apiKeyHeader)) {
            throw UnauthorizedException()
        }
    }
}