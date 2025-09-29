package io.github.tosuri13.kars

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.bedrock.BedrockClientSettings
import ai.koog.prompt.executor.clients.bedrock.BedrockModels
import ai.koog.prompt.executor.clients.bedrock.BedrockRegions
import ai.koog.prompt.executor.llms.all.simpleBedrockExecutor
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val agent = AIAgent(
        executor = simpleBedrockExecutor(
            System.getenv("AWS_ACCESS_KEY_ID"),
            System.getenv("AWS_SECRET_ACCESS_KEY"),
            settings = BedrockClientSettings(
                region = BedrockRegions.US_EAST_1.regionCode
            )
        ),
        llmModel = BedrockModels.AmazonNovaPremier,
    )
    val result = agent.run("こんにちは！")
    println(result)
}