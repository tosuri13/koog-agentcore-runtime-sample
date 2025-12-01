package io.github.tosuri13.kars.services

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.bedrock.BedrockLLMClient
import ai.koog.prompt.executor.clients.bedrock.BedrockModels
import ai.koog.prompt.executor.clients.bedrock.withInferenceProfile
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import aws.smithy.kotlin.runtime.collections.Attributes
import aws.smithy.kotlin.runtime.time.Instant
import io.github.tosuri13.kars.tools.WeatherForecastToolsRegistry
import io.github.tosuri13.kars.tools.chatAgentStrategy
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider

class AgentService {
    class JavaInstanceProfileCredentialsProvider : CredentialsProvider {
        private val provider = DefaultCredentialsProvider.builder().build()
        private val providerName = "JavaInstanceProfileCredentialsProvider"

        override suspend fun resolve(attributes: Attributes): Credentials {
            return when (val credentials = provider.resolveCredentials()) {
                is AwsSessionCredentials -> Credentials(
                    accessKeyId = credentials.accessKeyId(),
                    secretAccessKey = credentials.secretAccessKey(),
                    sessionToken = credentials.sessionToken(),
                    expiration = credentials.expirationTime()
                        .map { Instant.fromEpochSeconds(it.epochSecond, it.nano) }
                        .orElse(null),
                    providerName = providerName
                )

                is AwsBasicCredentials -> Credentials(
                    accessKeyId = credentials.accessKeyId(),
                    secretAccessKey = credentials.secretAccessKey(),
                    providerName = providerName
                )

                else -> error("Unsupported credentials type: ${credentials::class.simpleName}")
            }
        }
    }

    private val bedrockRuntimeClient = BedrockRuntimeClient {
        region = "ap-northeast-1"
        credentialsProvider = JavaInstanceProfileCredentialsProvider()
    }

    suspend fun processMessage(message: String): String {
        val agent = AIAgent(
            llmModel = BedrockModels.AnthropicClaude4_5Haiku.withInferenceProfile("jp"),
            promptExecutor = SingleLLMPromptExecutor(BedrockLLMClient(bedrockRuntimeClient)),
            strategy = chatAgentStrategy(),
            toolRegistry = WeatherForecastToolsRegistry,
        )
        return agent.run(message)
    }
}