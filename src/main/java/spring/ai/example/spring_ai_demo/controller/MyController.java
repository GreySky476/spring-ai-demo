package spring.ai.example.spring_ai_demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import spring.ai.example.spring_ai_demo.advisors.ReReadingAdvisor;

@RestController
public class MyController {

    private final ChatClient chatClient;

    public MyController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ai")
    String generation(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .call()
                .content();
    }

    @GetMapping("/ai/stream")
    public Flux<String> streamFlux(@RequestParam("userInput") Object userInput) {
        String inputStr = (String) userInput;
        return this.chatClient.prompt(inputStr).advisors(new ReReadingAdvisor(), SimpleLoggerAdvisor.builder().build()).stream().content();
    }

}
