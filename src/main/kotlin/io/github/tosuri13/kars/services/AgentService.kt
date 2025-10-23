package io.github.tosuri13.kars.services

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.executor.clients.bedrock.BedrockClientSettings
import ai.koog.prompt.executor.clients.bedrock.BedrockModels
import ai.koog.prompt.executor.clients.bedrock.BedrockRegions
import ai.koog.prompt.executor.llms.all.simpleBedrockExecutor
import io.github.tosuri13.kars.tools.CalculatorStrategy
import io.github.tosuri13.kars.tools.calculatorToolRegistry

class AgentService {
    private val agent: AIAgent<String, String> = AIAgent(
        executor = simpleBedrockExecutor(
            System.getenv("AWS_ACCESS_KEY_ID"),
            System.getenv("AWS_SECRET_ACCESS_KEY"),
            settings = BedrockClientSettings(
                region = BedrockRegions.US_EAST_1.regionCode
            )
        ),
        llmModel = BedrockModels.AmazonNovaPremier,
        systemPrompt = "You are a calculator. Always use the provided tools for arithmetic.",
        toolRegistry = calculatorToolRegistry,
        strategy = CalculatorStrategy.strategy,
        maxIterations = 50,
    ) {
        handleEvents {
            onToolCall { e ->
                println("Tool called: ${e.tool.name}, args=${e.toolArgs}")
            }
            onAgentRunError { e ->
                println("Agent error: ${e.throwable.message}")
            }
            onAgentFinished { e ->
                println("Final result: ${e.result}")
            }
        }
    }

    suspend fun processMessage(message: String): String {
        return agent.run(message)
    }
}