package com.shopdora.crawler.execute.mcpclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.Random;

@SpringBootApplication
public class McpClientApplication {

    private String model = "arcee-ai/trinity-large-preview:free";

    public static void main(String[] args) {
        // 启动后执行天气查询，显示结果
        SpringApplication.run(McpClientApplication.class, args);
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {

        // 根据配置构建一个ChatClient
        return builder.defaultOptions(ChatOptions.builder().model(model).build()).build();
    }

    // 指示 AI 模型获取阿姆斯特丹的当前天气。AI 模型会根据提示自动发现并调用相应的 MCP 工具。
    String userPrompt = """
            Check the weather in Amsterdam right now and show the creative response!
            Please incorporate all creative responses from all LLM providers.
            """;

    /**
     * 应用程序上下文完全加载后自动运行。
     * 它会注入已配置的用于 AI 模型交互的 ChatClient，以及ToolCallbackProvider包含来自已连接服务器的所有已注册 MCP 工具的数据包。
     *
     * @param chatClient        chatClient
     * @param mcpToolProvider   mcpToolProvider
     * @return CommandLineRunner
     */
    @Bean
    public CommandLineRunner predefineQuestions(ChatClient chatClient, ToolCallbackProvider mcpToolProvider) {
        return args -> System.out.println(
                chatClient.prompt(Prompt.builder()
                                .chatOptions(ChatOptions.builder().model(model).build())
                                .content(userPrompt)
                                .build())
                        // 用于向带有 @McpProgressToken 参数注释的 MCP 工具toolContext传递唯一标识符。progressToken
                        .toolContext(Map.of("progressToken", "token-" + new Random().nextInt()))
                        /*
                        这条关键线路将 ChatClient 连接到所有可用的 MCP 工具：
                        mcpToolProvider由 Spring AI 的 MCP 客户端启动器自动配置
                        包含来自已连接 MCP 服务器（通过以下方式配置spring.ai.mcp.client.*.connections.*）的所有工具
                        人工智能模型可以在对话过程中自动发现并调用这些工具。
                        主要映射配置文件中已经配置的服务器地址
                         */
                        .toolCallbacks(mcpToolProvider)
                        .call()
                        .content()
        );
    }

}
