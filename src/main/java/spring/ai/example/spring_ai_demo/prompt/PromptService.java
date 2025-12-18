package spring.ai.example.spring_ai_demo.prompt;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 提示词使用
 *
 */
@Service
public class PromptService {

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

}
