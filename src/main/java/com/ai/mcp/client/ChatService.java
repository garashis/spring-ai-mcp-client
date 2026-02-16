package com.ai.mcp.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder,
                       ChatMemory chatMemory) {
        // Add a memory advisor to the chat client
        var chatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();

        // Build the chat client
        chatClient = chatClientBuilder
                .defaultAdvisors(chatMemoryAdvisor)
                .build();
    }

    public Flux<String> chatStream(String userInput, String chatId) {
        return chatClient.prompt()
                .advisors(advisorSpec ->
                        advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId)
                )
                .user(userInput)
                .stream()
                .content();
    }
}