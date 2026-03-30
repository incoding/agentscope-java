/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.examples.multiagents.pipeline;

import io.agentscope.core.formatter.openai.OpenAIChatFormatter;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.OpenAIChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AgentScope DashScope model bean for pipeline sub-agents (AgentScopeAgent).
 */
@Configuration
public class PipelineModelConfig {

    @Bean
    public Model dashScopeChatModel() {
        String key = System.getenv("MOONSHOT_API_KEY");
        return OpenAIChatModel.builder().apiKey(key).modelName("kimi-k2.5")
                .baseUrl("https://api.moonshot.cn/v1")
                .stream(true)
                .formatter(new OpenAIChatFormatter())
                .build();
    }
}
