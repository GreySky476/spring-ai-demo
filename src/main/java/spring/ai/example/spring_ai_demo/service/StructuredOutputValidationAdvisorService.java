package spring.ai.example.spring_ai_demo.service;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * StructuredOutputValidationAdvisor
 * 根据生成的 JSON 架构验证结构化 JSON 输出，并在验证失败时重试调用，最多达到指定的尝试次数。
 * 主要特点：
 *      根据预期的输出类型自动生成 JSON 模式
 *      根据架构验证 LLM 响应
 *      如果验证失败，则重试调用，最多可配置的尝试次数
 *      在重试尝试时使用验证错误消息来增强提示，以帮助 LLM 更正其输出
 *      使用callAdvisorChain.copy(this)创建子链进行递归调用
 *      （可选）支持用于 JSON 处理的自定义 ObjectMapper
 */
@Service
public class StructuredOutputValidationAdvisorService {

    @Resource
    private ChatModel chatModel;

    public void structOutputValidationAdvisor() {
        StructuredOutputValidationAdvisor outputValidationAdvisor = StructuredOutputValidationAdvisor.builder()
                .outputType(MyOutputType.class)
                .maxRepeatAttempts(3)
                .advisorOrder(BaseAdvisor.HIGHEST_PRECEDENCE + 1000)
                .build();
        ChatClient client = ChatClient.builder(chatModel)
                .defaultAdvisors(outputValidationAdvisor)
                .build();
    }

    public static class MyOutputType {

    }
}
