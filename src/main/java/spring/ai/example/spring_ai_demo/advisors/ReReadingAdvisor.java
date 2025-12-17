package spring.ai.example.spring_ai_demo.advisors;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 *
 * https://arxiv.org/pdf/2309.06275
 *
 * Re-Reading (Re2) 的技术，可提高大型语言模型的推理能力
 * 模板：
 *      {Input_Query}
 *      Read the question again: {Input_Query}
 */
public class ReReadingAdvisor implements BaseAdvisor {

    private static final String DEFAULT_RE2_ADVISE_TEMPLATE = """
			{re2_input_query}
			重新读一遍问题: {re2_input_query}
			""";

    private final String re2AdviseTemplate;

    private int order = 0;

    public ReReadingAdvisor() {
        this(DEFAULT_RE2_ADVISE_TEMPLATE);
    }

    public ReReadingAdvisor(String re2AdviseTemplate) {
        this.re2AdviseTemplate = re2AdviseTemplate;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String inputQuery = PromptTemplate.builder()
                .template(this.re2AdviseTemplate)
                .variables(Map.of("re2_input_query", chatClientRequest.prompt().getUserMessage().getText()))
                .build()
                .render();
        return chatClientRequest.mutate()
                // 聚合消息
                .prompt(chatClientRequest.prompt().augmentUserMessage(inputQuery))
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 修改执行顺序，越小优先级越高
     */
    public ReReadingAdvisor withOrder(int order) {
        this.order = order;
        return this;
    }
}
