package spring.ai.example.spring_ai_demo.configuartion;

import com.google.common.collect.Lists;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 多数情况下都会使用多个模型，所以需要禁用 chatClient 的自动配置
 */
@Configuration
public class ChatClientConfig {

    @Value("${siliconflow.api-key}")
    private String siliconflowApi;

    @Value("${siliconflow.base-url}")
    private String siliconflowBaseUrl;

    // 自动注入
    @Bean
    public ChatClient openAiChatClient(ChatClient.Builder builder) {
        // 自定义记录的信息
//        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor(
//                request -> "Custom request: " + request.prompt().getUserMessage(),
//                response -> "Custom response: " + response.getResult(),
//                0
//        );
        ChatClient client = builder
                .defaultSystem("你将一直使用海盗的语气回答问题，回答的文字保持中文，规范需要遵守，但是回答需要保持中文")
                // 上下文敏感词添加拦截
                .defaultAdvisors(SafeGuardAdvisor.builder().sensitiveWords(Lists.newArrayList("越权", "跳过")).build())
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .defaultOptions(OpenAiChatOptions.builder().model("deepseek-ai/DeepSeek-R1-0528-Qwen3-8B").temperature(0.5).build())
                .build();
        // 元数据，提供上下文信息，可以对当前提问者做标记，适合追溯历史记录
//        String resp = client.prompt().user(u -> u.text("hello").metadata("messageId", "msg-123").metadata("userId", "user-456")).call().content();
//        System.out.println("metadata: " + resp);
        return client;
    }

    @Bean(name = "deepseekAi")
    public OpenAiApi deepseekAi() {
        return OpenAiApi.builder()
                .baseUrl(siliconflowBaseUrl)
                .apiKey(siliconflowApi)
                .build();
    }

    @Bean(name = "deepseekAiModel")
    public OpenAiChatModel deepseekAiModel(@Qualifier("deepseekAi") OpenAiApi deepseekAi) {
        return OpenAiChatModel.builder()
                .openAiApi(deepseekAi)
                .defaultOptions(OpenAiChatOptions.builder().model("deepseek-ai/DeepSeek-R1-0528-Qwen3-8B").temperature(0.5).build())
                .build();
    }

}
