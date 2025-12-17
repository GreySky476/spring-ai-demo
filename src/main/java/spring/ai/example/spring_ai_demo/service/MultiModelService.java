package spring.ai.example.spring_ai_demo.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 多模型使用
 */
@Service
public class MultiModelService {

    private static final Logger logger = LoggerFactory.getLogger(MultiModelService.class);

    @Autowired
    private OpenAiChatModel baseChatModel;

    @Autowired
    private OpenAiApi baseOpenAiApi;

    @Value("${siliconflow.api-key}")
    private String siliconflowApiKey;

    @Value("${openai.api-key}")
    private String openAiApiKey;

    @PostConstruct
    public void init() {
        // 调用多模型输出
//        multiClientFlow();
    }

    public void multiClientFlow() {
        try {
            // new OpenApi for Groq
            OpenAiApi groqApi = baseOpenAiApi.mutate()
                    .baseUrl("https://api.siliconflow.cn")
                    .apiKey(siliconflowApiKey)
                    .build();

            // new OpenApi for OpenAi
            OpenAiApi gptApi = baseOpenAiApi.mutate()
                    .baseUrl("https://api.openai.com")
                    .apiKey(openAiApiKey)
                    .build();

            // 构建 model
            OpenAiChatModel groqModel = baseChatModel.mutate()
                    .openAiApi(groqApi)
                    .defaultOptions(OpenAiChatOptions.builder().model("deepseek-ai/DeepSeek-R1-0528-Qwen3-8B").temperature(0.5).build())
                    .build();
            // gpt model
            OpenAiChatModel gptModel = baseChatModel.mutate()
                    .openAiApi(gptApi)
//                    .defaultOptions(OpenAiChatOptions.builder().model("gpt-3.5-turbo").temperature(0.5).build())
                    .build();

            String prompt = "What is the capital of France";

            String groqResponse = ChatClient.builder(groqModel).build().prompt(prompt).call().content();
            // 实体映射
            record ActorFilms(String actor, List<String> movies) {}
            ActorFilms gptResponse = ChatClient.builder(gptModel).build().prompt(prompt).call().entity(ActorFilms.class);
            logger.info("GPT response: {}, {}", gptResponse.actor, gptResponse.movies);

            logger.info("Groq response: {}", groqResponse);
            logger.info("GPT response: {}", gptResponse);

        } catch (Exception e) {
            logger.error("Error in muti-client flow", e);
        }
    }

}
