package spring.ai.example.spring_ai_demo.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChatAdvisorsService {

    private final static Logger logger = LoggerFactory.getLogger(ChatAdvisorsService.class);

    @Autowired
    @Qualifier("deepseekAi")
    private OpenAiApi deepseekAi;

    @Autowired
    @Qualifier("deepseekAiModel")
    private OpenAiChatModel baseChatModel;

//    @Autowired
//    private ChatClient client;

    @PostConstruct
    public void init() {
//        advisorsChat();
    }

    public void advisorsChat() {
        String userText = "hello";
        // 构建聊天记忆，最多 15 条
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(15).build();
        // 向量存储
//        VectorStore vectorStore = SimpleVectorStore.builder(new OpenAiEmbeddingModel(deepseekAi)).build();

        ChatClient client = ChatClient.builder(baseChatModel)
                .defaultAdvisors(
                        // 聊天记忆
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        // 从矢量存储中检索问题的上下文并将其添加到提示的用户文本中
//                        QuestionAnswerAdvisor.builder(vectorStore).build(),
                        SimpleLoggerAdvisor.builder().build()
                )
                .build();

        var conversationId = "678";

        String resp = client.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(userText)
                .call()
                .content();

        System.out.println("advisors: " + resp);
    }

}
