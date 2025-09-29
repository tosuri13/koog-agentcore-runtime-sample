package io.github.tosuri13.kars

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("ping") {
            call.respond(mapOf("status" to "Healthy"))
        }
        post("invocations") {
            call.respond(mapOf("message" to "Hello, World!!"))
        }
    }
}