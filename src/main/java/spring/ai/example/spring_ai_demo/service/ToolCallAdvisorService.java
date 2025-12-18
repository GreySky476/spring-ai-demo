package spring.ai.example.spring_ai_demo.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallAdvisor;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.tool.metadata.DefaultToolMetadata;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.stereotype.Service;

/**
 * ToolCallAdvisor
 *      将工具调用循环实现为顾问程序链的一部分，而不是依赖于模型的内部工具执行。 这使得链中的其他顾问能够拦截和观察工具调用过程。
 *      主要特点：
 *          通过设置 setInternalToolExecutionEnabled(false) 禁用模型的内部工具执行
 *          循环遍历顾问程序链，直到不再存在工具调用
 *          支持“直接返回”功能 - 当工具执行有 returnDirect=true 时，它会中断工具调用循环并将工具执行结果直接返回到客户端应用程序，而不是将其发送回 LLM
 *          使用callAdvisorChain.copy(this)创建子链进行递归调用
 *          包括空安全检查以处理聊天响应可能为空的情况
 *
 * 直接返回功能
 *      允许工具绕过 LLM 并将其结果直接返回到客户端应用程序
 *      使用场景：
 *         该工具的输出是最终答案，不需要LLM处理
 *         您希望通过避免额外的 LLM 调用来减少延迟
 *         工具结果应按原样返回，无需解释
 */
@Service
public class ToolCallAdvisorService {

    @Resource
    private ChatModel chatModel;


    @PostConstruct
    public void init() {

    }

    public void toolCallAdvisor() {
        ToolCallAdvisor toolCallAdvisor = ToolCallAdvisor.builder()
                .toolCallingManager(DefaultToolCallingManager.builder().build())
                .advisorOrder(BaseAdvisor.HIGHEST_PRECEDENCE + 300)
                .build();

        ChatClient client = ChatClient.builder(chatModel)
                .defaultToolCallbacks(MethodToolCallback.builder()
                        .toolMetadata(ToolMetadata.builder()
                                // 直接返回功能
                                .returnDirect(true).build()).build())
                .defaultAdvisors(toolCallAdvisor)
                .build();
    }


}
