package spring.ai.example.spring_ai_demo.prompt;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 提示词使用
 *
 */
@Service
public class PromptService {

    @Resource
    private ChatModel chatModel;

    @PostConstruct
    public void init() {
//        chatPrompt();
    }

    /**
     * stringTemplate 渲染器
     */
    public void stringTemplatePrompt() {
        PromptTemplate promptTemplate = PromptTemplate.builder()
                // 自定义渲染器
                .renderer(StTemplateRenderer.builder().startDelimiterToken('{').endDelimiterToken('}').build())
                .template("{input}")
                .build();
        // 渲染
        promptTemplate.render(Map.of("input", "hello world"));

    }

    public void chatPrompt() {
        String userText = """
                告诉我关于 ai 未来应用的方向，比如：{userInput1}、{userInput2}
                """;

        PromptTemplate userPromptTemplate = new PromptTemplate(userText);
        Message userMessage = userPromptTemplate.createMessage(Map.of("userInput1", "医药", "userInput2", "消费"));

        String systemText = """
                你是一个 {systemInput} 助手，请根据用户输入给出一个 ai 应用方向，请使用中文回答。
                """;

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);

        Message systemMessage = systemPromptTemplate.createMessage(Map.of("systemInput", "ai"));

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        List<Generation> results = chatModel.call(prompt).getResults();

        System.out.println(results);
    }

}
