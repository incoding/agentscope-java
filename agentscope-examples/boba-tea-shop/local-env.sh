#!/bin/bash
# =============================================================================
# AgentScope Boba Tea Shop - Local Environment Variables
# =============================================================================
#
# Generated from local-env.example, configured for kimi-k2.5 model
# =============================================================================

# ============================================================================
# Java Configuration
# ============================================================================
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
export M2_HOME=/Users/wk/program/apache-maven-3.9.9
export PATH=$M2_HOME/bin:$PATH
export JAVA_TOOL_OPTIONS="-Djdk.httpclient.version=HTTP_1_1"
export MODEL_STREAM_ENABLED=false

# ============================================================================
# MySQL Configuration
# ============================================================================
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=multi_agent_demo
export DB_USERNAME=multi_agent_demo
export DB_PASSWORD=multi_agent_demo@321

# ============================================================================
# Nacos Configuration
# ============================================================================
export NACOS_SERVER_ADDR=localhost:8848
export NACOS_NAMESPACE=public
export NACOS_REGISTER_ENABLED=true

# ============================================================================
# LLM Model Configuration - Kimi K2.5 (OpenAI Compatible)
# ============================================================================
export MODEL_PROVIDER=openai
export MODEL_API_KEY=${MOONSHOT_API_KEY:-your_moonshot_api_key_here}
export MODEL_NAME=kimi-k2.5
export MODEL_BASE_URL=https://api.moonshot.cn/v1

# ============================================================================
# Bailian Knowledge Base Configuration (RAG)
# ============================================================================
export DASHSCOPE_ACCESS_KEY_ID=your_access_key_id_here
export DASHSCOPE_ACCESS_KEY_SECRET=your_access_key_secret_here
export DASHSCOPE_WORKSPACE_ID=your_workspace_id_here
export DASHSCOPE_INDEX_ID=your_index_id_here

# ============================================================================
# Mem0 Memory Service Configuration
# Leave empty to disable long-term memory locally
# ============================================================================
export MEM0_API_KEY=

# ============================================================================
# Service Port Configuration (Optional - default values are fine)
# ============================================================================
# export BUSINESS_MCP_SERVER_PORT=10002
# export CONSULT_SUB_AGENT_PORT=10005
# export BUSINESS_SUB_AGENT_PORT=10006
# export SUPERVISOR_AGENT_PORT=10008
