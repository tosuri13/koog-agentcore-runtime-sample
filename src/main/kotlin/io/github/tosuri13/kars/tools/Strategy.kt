package io.github.tosuri13.kars.tools

import ai.koog.agents.core.agent.entity.AIAgentGraphStrategy
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*

fun chatAgentStrategy(): AIAgentGraphStrategy<String, String> = strategy("chatAgentStrategy") {
    val callLLM by nodeLLMRequestMultiple()
    val executeTool by nodeExecuteMultipleTools()
    val sendToolResult by nodeLLMSendMultipleToolResults()

    edge(nodeStart forwardTo callLLM)
    edge(callLLM forwardTo executeTool onMultipleToolCalls { true })
    edge((callLLM forwardTo nodeFinish) transformed { it.first() } onAssistantMessage { true })

    edge(executeTool forwardTo sendToolResult)
    edge(sendToolResult forwardTo executeTool onMultipleToolCalls { true })
    edge((sendToolResult forwardTo nodeFinish) transformed { it.first() } onAssistantMessage { true })
}
