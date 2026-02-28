package spring.ai.example.spring_ai_demo.configuartion;

import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import spring.ai.example.spring_ai_demo.tool.AiTool;

/**
 * 多数情况下都会使用多个模型，所以需要禁用 chatClient 的自动配置
 * 有多个模型配置时，建议从 db 中读取并统一初始化，一般参数有
 * - 模型名称
 * - 模型id
 * - 平台 apikey
 * - 模型平台 api 地址
 * - 调用接口（这个根据平台不同，实际调用时接口名称不固定，建议灵活些，不过一般遵守 openai 的话接口名称一样，主要区别在于 api 地址拼接时会出现问题）
 * - 输入 token 计费
 * - 输出 token 计费
 * - 图像 token 计费
 * - 音频 token 计费
 * - tool 使用（有些模型可能并不支持 tool 工具使用）
 * - json 格式返回（新模型支持概率较高，旧模型或小模型可能并不支持）
 * ### 以上计费一般只用记录输入、输出 token，特殊模型特殊处理
 * - 模型特征（该模型擅长哪方面的）
 * ### 特征细分，可以根据模型特征进行细分
 * - 编码、深度思考（擅长复杂任务）、翻译、图像
 * ###
 * - 免费、付费
 * - qps 限制（一般没有限制，但实际调用时免费模型一般经不起高频调用，建议 1qps）
 *
 * 模型有输入、输出 token 区别，定价一般不同，以 money/M tokens 为单位，m 为百万 token
 * token 为模型计算 token 的数量，输入的问题等会统一转换成token，模型转换 token 的数量会因模型而异
 *
 */
@Configuration
public class ChatClientConfig {

//    @Value("${siliconflow.api-key}")
//    private String siliconflowApi;

//    @Value("${siliconflow.base-url}")
//    private String siliconflowBaseUrl;

    @Value("${openRouter.model:z-ai/glm-4.5-air:free}")
    private String openRouterModel;

    @Value("${openRouter.openai.api-key}")
    private String openRouterApiKey;

    @Value("${openRouter.openai.base-url}")
    private String openRouterBaseUrl;

    @Resource
    private ChatMemory chatMemory;

    @Bean(name = "openRouterApi")
    public OpenAiApi openRouterApi() {
        return OpenAiApi.builder()
                .baseUrl(openRouterBaseUrl)
                .apiKey(openRouterApiKey)
                .completionsPath("/chat/completions")
                .embeddingsPath("/embeddings")
                .build();
    }

    // 自动注入
    @Bean
    public ChatClient openAiChatClient(@Qualifier("openRouterApi") OpenAiApi openRouterApi) {
        // 自定义记录的信息
        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor(
                request -> "Custom request: " + request.prompt().getUserMessage(),
                response -> "Custom response: " + response.getResult(),
                0
        );
        ChatClient client = ChatClient.builder(OpenAiChatModel.builder().openAiApi(openRouterApi).build())
                .defaultSystem("""
                        你将使用海盗语气的中文回答我提出的问题
                        """)
                // 上下文敏感词添加拦截
                .defaultAdvisors(SafeGuardAdvisor.builder().sensitiveWords(Lists.newArrayList("越权", "跳过")).build())
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                // 聊天记忆
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                // 本机结构化输出
//                .defaultAdvisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .defaultOptions(OpenAiChatOptions.builder().model(openRouterModel).build())
                .build();
        // 元数据，提供上下文信息，可以对当前提问者做标记，适合追溯历史记录
        String resp = client.prompt().user(u -> u.text("hello").metadata("messageId", "msg-123").metadata("userId", "user-456")).call().content();
        System.out.println("metadata: " + resp);
        return client;
    }

//    @Bean(name = "deepseekAi")
//    public OpenAiApi deepseekAi() {
//        return OpenAiApi.builder()
//                .baseUrl(siliconflowBaseUrl)
//                .apiKey(siliconflowApi)
//                .build();
//    }

//    @Bean(name = "deepseekAiModel")
//    public OpenAiChatModel deepseekAiModel(@Qualifier("deepseekAi") OpenAiApi deepseekAi) {
//        return OpenAiChatModel.builder()
//                .openAiApi(deepseekAi)
//                .defaultOptions(OpenAiChatOptions.builder().model("deepseek-ai/DeepSeek-R1-0528-Qwen3-8B").temperature(0.5).build())
//                .build();
//    }

}
