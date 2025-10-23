package io.github.tosuri13.kars

import io.github.tosuri13.kars.services.AgentService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AgentRequest(val message: String)

@Serializable
data class AgentResponse(val message: String)

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) { json() }

    val agentService = AgentService()

    routing {
        get("ping") {
            call.respond(mapOf("status" to "Healthy"))
        }
        post("invocations") {
            try {
                val request = call.receive<AgentRequest>()
                val response = agentService.processMessage(request.message)

                call.respond(AgentResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(mapOf("error" to e.message, "stackTrace" to e.stackTraceToString()))
            }
        }
    }
}