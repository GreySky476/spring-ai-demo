package spring.ai.example.spring_ai_demo.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class ChatClientExample {

//    @Bean
    CommandLineRunner cli(@Qualifier("openAiChatClient") ChatClient openAiChatClient) {
        return args -> {
            var scanner = new Scanner(System.in);

            ChatClient chatClient;

            System.out.println("\nWhich chat client would you like to use?");
            System.out.println("1. OpenAI");
            System.out.println("2. Cohere");
            System.out.println("enter your choice (1 or 2)");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                chatClient = openAiChatClient;
                System.out.println("Using OpenAI");
            } else {
                chatClient = openAiChatClient;
                System.out.println("Using Cohere");
            }

            System.out.println("\nEnter your question: ");
            String input = scanner.nextLine();
            String response = chatClient.prompt(input).call().content();
            System.out.println("ASSISTANT: " + response);

            scanner.close();
        };
    }

}
