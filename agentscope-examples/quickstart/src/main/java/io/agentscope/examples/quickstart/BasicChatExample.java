/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agentscope.examples.quickstart;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.formatter.openai.OpenAIChatFormatter;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.model.OpenAIChatModel;
import io.agentscope.core.tool.Toolkit;

/**
 * BasicChatExample - The simplest Agent conversation example.
 */
public class BasicChatExample {

    public static void main(String[] args) throws Exception {
        // Print welcome message
        ExampleUtils.printWelcome(
                "Basic Chat Example",
                "This example demonstrates the simplest Agent setup.\n"
                        + "You'll chat with an AI assistant powered by DashScope.");

        // Get API key (from environment or interactive input)
        String apiKey = ExampleUtils.getApiKey(
                "MOONSHOT_API_KEY", "Moonshot", "https://platform.moonshot.cn");

        // Create Agent with minimal configuration
        ReActAgent agent =
                ReActAgent.builder()
                        .name("Assistant")
                        .sysPrompt("You are a helpful AI assistant. Be friendly and concise.")
                        .model(
                                OpenAIChatModel.builder()
                                        .apiKey(apiKey)
                                        .modelName("kimi-k2.5")
                                        .baseUrl("https://api.moonshot.cn/v1")
                                        .stream(true)
                                        .formatter(new OpenAIChatFormatter())
                                        .build())
                        .memory(new InMemoryMemory())
                        .toolkit(new Toolkit())
                        .build();

        // Start interactive chat
        ExampleUtils.startChat(agent);
    }
}
