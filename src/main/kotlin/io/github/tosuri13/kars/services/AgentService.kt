package io.github.tosuri13.kars.services

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.executor.clients.bedrock.BedrockLLMClient
import ai.koog.prompt.executor.clients.bedrock.BedrockModels
import ai.koog.prompt.executor.clients.bedrock.withInferenceProfile
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import aws.sdk.kotlin.runtime.auth.credentials.DefaultChainCredentialsProvider
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import io.github.tosuri13.kars.tools.WeatherForecastToolsRegistry
import io.github.tosuri13.kars.tools.chatAgentStrategy

class AgentService {
    private val bedrockRuntimeClient = BedrockRuntimeClient {
        region = "ap-northeast-1"
        credentialsProvider = DefaultChainCredentialsProvider()
    }

    suspend fun processMessage(message: String): String {
        val agent = AIAgent(
            llmModel = BedrockModels.AnthropicClaude4_5Haiku.withInferenceProfile("jp"),
            promptExecutor = SingleLLMPromptExecutor(
                BedrockLLMClient(bedrockRuntimeClient)
            ),
            strategy = chatAgentStrategy(),
            toolRegistry = WeatherForecastToolsRegistry,
        ) {
            handleEvents {
                onNodeExecutionStarting { e ->
                    println(">>> Node starting: ${e.node.name}")
                }
                onNodeExecutionCompleted { e ->
                    println("<<< Node completed: ${e.node.name}")
                }
                onLLMCallStarting { e ->
                    println("=== LLM Call Starting ===")
                }
                onLLMCallCompleted { e ->
                    println("=== LLM Call Completed ===")
                    println("Response: ${e.responses}")
                }
                onToolCallStarting { e ->
                    println("!!! Tool call starting: ${e.tool.name}")
                }
                onToolCallCompleted { e ->
                    println("!!! Tool call completed: ${e.tool.name}, args=${e.toolArgs}")
                    println("Result: ${e.result}")
                }
                onAgentCompleted { e ->
                    println("Final result:\n${e.result}")
                }
            }
        }

        return agent.run(message)
    }
}