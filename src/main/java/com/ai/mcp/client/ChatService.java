package com.ai.mcp.client;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Random;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder,
                       ChatMemory chatMemory, ToolCallbackProvider tools) {
        // Add a memory advisor to the chat client
        var chatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();

        // Build the chat client
        chatClient = chatClientBuilder
                .defaultAdvisors(chatMemoryAdvisor, new SimpleLoggerAdvisor())
                .defaultToolCallbacks(tools)

                .build();
    }

    public Flux<String> chatStream(String userInput, String chatId) {
        return chatClient.prompt()
                .advisors(advisorSpec ->
                        advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId)
                )
                .toolContext(Map.of("progressToken", 2))
                .user(userInput)
                .stream()
                .content();
    }
}