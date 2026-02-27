package spring.ai.example.spring_ai_demo.service;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

/**
 * 封装模型调用
 *
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    @Resource
    private ChatClient chatClient;


    /**
     * @param requestId     请求ID
     * @param modelId       模型ID
     * @param systemMessage 系统消息，一般用于给模型定义角色，比如你是一个 xxxx ，请帮我完成 xxx等
     * @param prompt        具体问题等
     * @return 模型输出结果，自定自定义相关类
     */
    public Object chat(String requestId, String modelId, String systemMessage, String prompt) {
        ChatResponse chatResponse = chatClient.prompt(Prompt.builder()
                        .chatOptions(
                                ChatOptions.builder()
                                        // 构建模型参数
                                        .model(modelId)
                                        .build())
                        // 用户消息
                        .messages(UserMessage.builder().text(prompt).build())
                        .build())
                .system(systemMessage)
                .call()
                .chatResponse();

        if (chatResponse == null) {
            log.error("chatResponse is null, requestId:{}", requestId);
            throw new RuntimeException("chatResponse is null, requestId:" + requestId);
        }
        ChatResponseMetadata metadata = chatResponse.getMetadata();

        log.info("chatResponse, requestId:{} metadata{}", requestId, chatResponse);
        // generationId 链路 id
        String id = metadata.getId();
        // model 模型名称
        String model = metadata.getModel();
        // inputTokens 模型输入 token
        Integer promptTokens = metadata.getUsage().getPromptTokens();
        // outputTokens 模型输出 token
        Integer completionTokens = metadata.getUsage().getCompletionTokens();
        // totalTokens 总消耗 token
        Integer totalTokens = metadata.getUsage().getTotalTokens();
        // ai 输出结果
        String text = chatResponse.getResult().getOutput().getText();
        return null;
    }

}
