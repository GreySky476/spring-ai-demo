package com.shopdora.crawler.execute.mcpclient.handler;

import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springaicommunity.mcp.annotation.McpProgress;
import org.springaicommunity.mcp.annotation.McpSampling;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * MCP 客户端处理器
 * 如果连接多个服务器，clients = {"xxxx", "xxxxxx"}
 */
@Service
public class McpClientHandlers {
    private static final Logger logger = LoggerFactory.getLogger(McpClientHandlers.class);

    private final ChatClient chatClient;

    public McpClientHandlers(@Lazy ChatClient chatClient) { // Lazy is needed to avoid circular dependency
        this.chatClient = chatClient;
    }

    /**
     * 进度处理器
     *
     * @param progressNotification 接收服务器长时间运行操作的实时进度更新
     */
    @McpProgress(clients = "my-weather-server")
    public void progressHandler(McpSchema.ProgressNotification progressNotification) {
        logger.info("MCP PROGRESS: [{}], progress: {} total: {} message: {}",
                progressNotification.progressToken(), progressNotification.progress(),
                progressNotification.total(), progressNotification.message());
    }

    /**
     * 日志处理器
     *
     * @param loggingMessageNotification 接收来自服务器的结构化日志消息，用于调试和监控
     */
    @McpLogging(clients = "my-weather-server")
    public void loggingHandler(McpSchema.LoggingMessageNotification loggingMessageNotification) {
        logger.info("MCP LOGGING: [{}] {}", loggingMessageNotification.level(), loggingMessageNotification.data());
    }

    /**
     * 最强大的功能。它使服务器能够从客户端的 LLM 请求 AI 生成的内容。用于双向 AI 交互、创意内容生成和动态响应。
     *
     * @param llmRequest 服务器 llm 请求
     * @return llm 响应
     */
    @McpSampling(clients = "my-weather-server")
    public McpSchema.CreateMessageResult samplingHandler(McpSchema.CreateMessageRequest llmRequest) {
        logger.info("MCP SAMPLING: {}", llmRequest);

        String llmResponse = chatClient
                .prompt()
                .system(llmRequest.systemPrompt())
                .user(((McpSchema.TextContent) llmRequest.messages().get(0).content()).text())
                .call()
                .content();

        return McpSchema.CreateMessageResult.builder().content(new McpSchema.TextContent(llmResponse)).build();

    }

}
