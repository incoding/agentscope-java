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
package io.agentscope.examples.quickstart.rag;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.user.UserAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.model.OpenAIChatModel;
import io.agentscope.core.rag.RAGMode;
import io.agentscope.core.rag.integration.ragflow.RAGFlowConfig;
import io.agentscope.core.rag.integration.ragflow.RAGFlowKnowledge;
import io.agentscope.examples.quickstart.ExampleUtils;

/**
 * Example demonstrating how to use RAGFlow Knowledge Base for RAG.
 */
public class RAGFlowRAGExample {

    public static void main(String[] args) throws Exception {
        // Check environment variables
        String ragflowApiKey = System.getenv("RAGFLOW_API_KEY");
        String ragflowBaseUrl = System.getenv("RAGFLOW_BASE_URL");
        String datasetId = System.getenv("RAGFLOW_DATASET_ID");
        String apiKey = ExampleUtils.getApiKey(
                "MOONSHOT_API_KEY", "Moonshot", "https://platform.moonshot.cn");

        if (ragflowApiKey == null || ragflowBaseUrl == null || datasetId == null) {
            System.err.println("Error: Required environment variables not set.");
            System.err.println("Please set the following environment variables:");
            System.err.println("  - RAGFLOW_API_KEY");
            System.err.println("  - RAGFLOW_BASE_URL");
            System.err.println("  - RAGFLOW_DATASET_ID");
            System.exit(1);
        }

        ReActAgent agent =
                ReActAgent.builder()
                        .name("KnowledgeAssistant")
                        .model(
                                OpenAIChatModel.builder()
                                        .apiKey(apiKey)
                                        .modelName("kimi-k2.5")
                                        .baseUrl("https://api.moonshot.cn/v1")
                                        .build())
                        .knowledge(
                                RAGFlowKnowledge.builder()
                                        .config(
                                                RAGFlowConfig.builder()
                                                        .apiKey(ragflowApiKey)
                                                        .baseUrl(ragflowBaseUrl)
                                                        .addDatasetId(datasetId)
                                                        .build())
                                        .build())
                        .ragMode(RAGMode.AGENTIC)
                        .build();

        UserAgent userAgent = UserAgent.builder().name("User").build();

        Msg msg = null;
        while (true) {
            msg = userAgent.call(msg).block();
            if (msg.getTextContent().equals("exit")) {
                break;
            }
            msg = agent.call(msg).block();
        }
    }
}
