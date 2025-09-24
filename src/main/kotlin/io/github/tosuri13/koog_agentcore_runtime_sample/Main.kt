package io.github.tosuri13.koog_agentcore_runtime_sample

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
        llmModel = BedrockModels.AmazonNovaMicro,
    )
    val result = agent.run("こんにちは！")
    println(result)

//    BedrockRuntimeClient {
//        region = "us-east-1"
//    }.use { client ->
//        val agent = AIAgent(
//            executor = SingleLLMPromptExecutor(BedrockLLMClient(client)),
//            llmModel = LLModel(
//                provider = LLMProvider.Bedrock,
//                id = "us.anthropic.claude-sonnet-4-20250514-v1:0",
//                capabilities = listOf(
//                    LLMCapability.Completion,
//                    LLMCapability.Temperature
//                ),
//                contextLength = 100_000,
//                maxOutputTokens = 4096,
//            ),
//            temperature = 0.0
//        )
//
//        val result = agent.run("こんにちは！")
//        println(result)
//    }
}