package spring.ai.example.spring_ai_demo.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import spring.ai.example.spring_ai_demo.advisors.ReReadingAdvisor;

@Service
public class MultimodalityService {

    @Resource
    private ChatClient client;

    @PostConstruct
    public void init() {
//        multimodality();
    }

    public void multimodality() {
        ClassPathResource resource = new ClassPathResource("img/img.png");

        UserMessage message = UserMessage.builder()
                .text("请分析图片，并描述图片的画面")
                .media(new Media(MimeTypeUtils.IMAGE_PNG, resource))
                .build();

        Flux<String> content = client.prompt(new Prompt(message))
                .advisors(new ReReadingAdvisor())
                .stream()
                .content();

        content.subscribe(System.out::print);
    }


}
