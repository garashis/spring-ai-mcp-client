package com.ai.mcp.client;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ChatService {

    private final ChatClient chatClient;
    @Autowired
    private ApplicationContext applicationContext;

    public ChatService(OpenAiChatModel openAiChatModel,
                       ChatMemory chatMemory, ToolCallbackProvider tools, List<McpAsyncClient> mcpAsyncClients) {
        // Add a memory advisor to the chat client
        var chatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();

        // Build the chat client
        chatClient =  ChatClient.builder(openAiChatModel)
                .defaultAdvisors(chatMemoryAdvisor, new SimpleLoggerAdvisor())
                .defaultToolCallbacks(tools)
                .build();

        //set logging level
        mcpAsyncClients.forEach(mcpAsyncClient -> mcpAsyncClient.setLoggingLevel(McpSchema.LoggingLevel.DEBUG).block());
//        mcpAsyncClients.forEach(mcpAsyncClient -> System.out.println(mcpAsyncClient.getClientCapabilities()));
    }

    public Flux<String> chatStream(String userInput, String chatId) {
        //printBeans();

        return chatClient.prompt()
                .advisors(advisorSpec ->
                        advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId)
                )
                .toolContext(Map.of("progressToken", 2))
                .user(userInput)
                .stream()
                .content();
    }

    public void printBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beanNames);

        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            System.out.println(beanName + " -> " + bean.getClass().getName());
        }
    }
}