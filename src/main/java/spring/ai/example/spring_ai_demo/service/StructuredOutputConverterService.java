package spring.ai.example.spring_ai_demo.service;

import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * 结构化输出转换
 *
 */
@Service
public class StructuredOutputConverterService {

    @Resource
    private ChatClient client;

    @PostConstruct
    public void init() {
        beanStructOutputConverter();
    }

    /**
     * ai 输出结果转换成 bean
     */
    public void beanStructOutputConverter() {
        // 使用 bean 转换 ai 输出结果
        StructuredOutputConverter<OutputBean> outputConverter = new BeanOutputConverter<>(OutputBean.class);

        String userInput = """
                畅想未来五十年，科技大爆发，生活将有什么样的改变？
                {format}
                {end}
                """;

        HashMap<String, @Nullable Object> hashMap = Maps.newHashMap();
        hashMap.put("format", outputConverter.getFormat());
        hashMap.put("end", "回答的文字将使用中文，其它保持不变");
        // 提示词构建
        Prompt prompt = new Prompt(
                PromptTemplate.builder()
                        .template(userInput)
                        .variables(hashMap)
                        .build().createMessage()
        );

        Flux<String> content = client.prompt(prompt).stream().content();
        content.subscribe(System.out::print);
    }

    /**
     * 输出 bean
     */
    public static class OutputBean {
        private String name;
        private String message;
        private String dateTime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }
    }
}
