package io.github.tosuri13.kars.tools

import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.environment.ReceivedToolResult


object CalculatorStrategy {
    private const val MAX_TOKENS_THRESHOLD = 1000

    val strategy = strategy<String, String>("test") {
        val callLLM by nodeLLMRequestMultiple()
        val executeTools by nodeExecuteMultipleTools(parallelTools = true)
        val sendToolResults by nodeLLMSendMultipleToolResults()
        val compressHistory by nodeLLMCompressHistory<List<ReceivedToolResult>>()

        edge(nodeStart forwardTo callLLM)
        edge((callLLM forwardTo nodeFinish) transformed { it.first() } onAssistantMessage { true })
        edge((callLLM forwardTo executeTools) onMultipleToolCalls { true })

        edge((executeTools forwardTo compressHistory) onCondition {
            llm.readSession { prompt.latestTokenUsage > MAX_TOKENS_THRESHOLD }
        })
        edge((executeTools forwardTo sendToolResults) onCondition {
            llm.readSession { prompt.latestTokenUsage <= MAX_TOKENS_THRESHOLD }
        })
        edge(compressHistory forwardTo sendToolResults)

        edge((sendToolResults forwardTo executeTools) onMultipleToolCalls { true })
        edge((sendToolResults forwardTo nodeFinish) transformed { it.first() } onAssistantMessage { true })
    }
}