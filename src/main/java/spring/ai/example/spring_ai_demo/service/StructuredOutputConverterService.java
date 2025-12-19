package spring.ai.example.spring_ai_demo.service;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结构化输出转换
 *
 */
@Service
public class StructuredOutputConverterService {

    @Resource
    private ChatClient client;

    @Resource
    private ChatModel chatModel;

    @PostConstruct
    public void init() {
//        beanStructOutputConverter();
//        beanStructOutputConverterByOrder();
        nativeStructOutputConverter();
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
        // 提示 ai 使用 json 格式返回数据
        hashMap.put("format", outputConverter.getFormat());
        hashMap.put("end", "回答的文字将使用中文，其它保持不变");
        // 提示词构建
        Prompt prompt = new Prompt(
                PromptTemplate.builder()
                        .template(userInput)
                        .variables(hashMap)
                        .build()
                        .createMessage()
        );

        Flux<String> content = client.prompt(prompt).stream().content();
        content.subscribe(System.out::print);
        // 直接指定 bean 输出结果
        OutputBean outputBean = client.prompt(prompt).call().entity(OutputBean.class);
        System.out.println(outputBean);
    }

    /**
     * ai 输出结果转换成 bean
     */
    public void beanStructOutputConverter2() {
        // 使用 bean 转换 ai 输出结果
        StructuredOutputConverter<OutputBean> outputConverter = new BeanOutputConverter<>(OutputBean.class);

        String userInput = """
                畅想未来五十年，科技大爆发，生活将有什么样的改变？
                {format}
                {end}
                """;

        HashMap<String, @Nullable Object> hashMap = Maps.newHashMap();
        // 提示 ai 使用 json 格式返回数据
        hashMap.put("format", outputConverter.getFormat());
        hashMap.put("end", "回答的文字将使用中文，其它保持不变");
        // 提示词构建
        Prompt prompt = new Prompt(
                PromptTemplate.builder()
                        .template(userInput)
                        .variables(hashMap)
                        .build()
                        .createMessage()
        );

        Generation result = chatModel.call(prompt).getResult();
        // 手动处理后转换结果
        assert result.getOutput().getText() != null;
        OutputBean convert = outputConverter.convert(result.getOutput().getText());

        System.out.println(convert);
    }

    /**
     * ai 输出多列表结果且属性按照特定顺序排列
     */
    public void beanStructOutputConverterByOrder() {
        // 使用 JsonPropertyOrder 指定属性排列顺序
        @JsonPropertyOrder({"author", "names"})
        record Movie(String author, List<String> names) {}
        // 使用 bean 转换 ai 输出结果
        StructuredOutputConverter<List<Movie>> outputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<>() {
        });

        String userInput = """
                请推荐三位人生必看导演，并各列举五部经典的影片名字
                {format}
                {end}
                """;

        HashMap<String, @Nullable Object> hashMap = Maps.newHashMap();
        // 提示 ai 使用 json 格式返回数据
        hashMap.put("format", outputConverter.getFormat());
        hashMap.put("end", "回答的文字将使用中文，其它保持不变");
        // 提示词构建
        Prompt prompt = new Prompt(
                PromptTemplate.builder()
                        .template(userInput)
                        .variables(hashMap)
                        .build()
                        .createMessage()
        );

        Flux<String> content = client.prompt(prompt).stream().content();
        content.subscribe(System.out::print);
    }

    /**
     * 使用 ai 本身结构化输出
     */
    public void nativeStructOutputConverter() {

        String userInput = """
                请生成一个 JSON 格式的数据，包含三个字段：name, message, dateTime
                {end}
                """;

        HashMap<String, @Nullable Object> hashMap = Maps.newHashMap();
        // 提示 ai 使用 json 格式返回数据
        hashMap.put("end", "回答的文字将使用中文，其它保持不变");

        // 提示词构建
        Prompt prompt = new Prompt(
                PromptTemplate.builder()
                        .template(userInput)
                        .variables(hashMap)
                        .build()
                        .createMessage()
        );
        Flux<String> content = client.prompt(prompt)
                // 使用 ai 本身的结构化进行输出，可以手动指定，也可以在初始化 client 直接添加
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .stream()
                .content();

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
