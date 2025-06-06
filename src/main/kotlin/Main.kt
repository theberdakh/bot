package org.theberdakh

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

const val TOKEN = "6585317294:AAG9DyVCJhdQlwK2qrNb3d2vaLbt1pRdiq4"

fun main() {
    val botHandler = TelegramBotHandler()

    embeddedServer(Netty, port = Config.PORT, host = "0.0.0.0") {
        configureSerialization()
        configureCORS()

        routing {
            get("/") {
                call.respondText(
                    """
                    🤖 Telegram Bot Server
                    
                    Status: Running
                    Port: ${Config.PORT}
                    Webhook URL: ${Config.WEBHOOK_URL}
                    
                    Endpoints:
                    • GET / - This status page
                    • POST /webhook - Telegram webhook
                    • GET /health - Health check
                    • POST /set-webhook - Set webhook (for setup)
                    """.trimIndent(),
                    ContentType.Text.Plain
                )
            }

            get("/health") {
                call.respond(
                    HttpStatusCode.OK,
                    mapOf("status" to "healthy", "timestamp" to System.currentTimeMillis())
                )
            }

            post("/webhook") {
                try {
                    val body = call.receiveText()
                    println("Received webhook: $body")

                    val success = botHandler.processWebhookUpdate(body)

                    if (success) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to process update"))
                    }
                } catch (e: Exception) {
                    println("Webhook error: ${e.message}")
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            post("/set-webhook") {
                try {
                    botHandler.setupWebhook()
                    call.respond(HttpStatusCode.OK, mapOf("status" to "webhook set"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
        }
    }.start(wait = true)
}


fun Application.configureSerialization() {
    install(ContentNegotiation) {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }
    }
}

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        anyHost()
    }
}