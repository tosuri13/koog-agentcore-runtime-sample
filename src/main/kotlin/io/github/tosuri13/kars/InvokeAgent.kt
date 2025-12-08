package io.github.tosuri13.kars

import aws.sdk.kotlin.services.bedrockagentcore.BedrockAgentCoreClient
import aws.sdk.kotlin.services.bedrockagentcore.model.InvokeAgentRuntimeRequest
import aws.smithy.kotlin.runtime.content.decodeToString
import io.github.cdimascio.dotenv.dotenv
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend fun main() {
    val dotenv = dotenv()

    BedrockAgentCoreClient { region = "ap-northeast-1" }.use { client ->
        val message = client.invokeAgentRuntime(
            input = InvokeAgentRuntimeRequest {
                agentRuntimeArn = dotenv["AGENT_RUNTIME_ARN"]
                contentType = "application/json"
                payload = """{"message": "東京の天気を教えてほしい！"}""".encodeToByteArray()
            },
            block = { response ->
                val responseBody = response.response!!.decodeToString()
                Json.parseToJsonElement(responseBody).jsonObject["message"]?.jsonPrimitive?.content
            }
        )
        println(message)
    }
}
