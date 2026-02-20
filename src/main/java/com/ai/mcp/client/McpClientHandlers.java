package com.ai.mcp.client;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springaicommunity.mcp.annotation.McpProgress;
import org.springaicommunity.mcp.annotation.McpSampling;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.modelcontextprotocol.spec.McpSchema.*;

@Component
@RequiredArgsConstructor
public class McpClientHandlers {
    private final ChatClient.Builder chatClientBuilder;
    @McpLogging(clients = "server1")
    public Mono<Void> handleLoggingMessageMono(LoggingMessageNotification notification) {
        System.out.println("handleLoggingMessage log: " + notification.level() +
                " - " + notification.data());
        return Mono.empty();
    }

    @McpLogging(clients = "server1")
    public Mono<Void>  handleLoggingMessageWithParams(LoggingLevel level, String logger, String data) {
        System.out.println("handleLoggingMessageWithParams log: " + data);
        return Mono.empty();

    }

    @McpSampling(clients = "server1")
    public Mono<CreateMessageResult> handleSamplingRequest(CreateMessageRequest request) {
        // Process the request and generate a response
        // Build the chat client
        ChatClient chatClient = chatClientBuilder
                //.defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultSystem(request.systemPrompt())
                .build();
        String response = "My epic poem";
//        String response = chatClient.prompt()
//                .user(request.messages().get(0).content().toString())
//                //.stream()
//                .call()
//                .content();

        //response.col

        return Mono.just(CreateMessageResult.builder()
                .role(Role.ASSISTANT)
                .content(new TextContent(response))
                .model("gpt-4")
                .build());
    }

    @McpProgress(clients = "server1")
    public Mono<Void> handleProgressNotification2(ProgressNotification notification) {
        double percentage = notification.progress() * 100;
        System.out.println(String.format("handleProgressNotification2: %.2f%% - %s",
                percentage, notification.message()));
        return Mono.empty();
    }

//    @McpToolListChanged(clients = "my-mcp-client")
//    public void handleToolListChanged(List<McpSchema.Tool> updatedTools) {
//        System.out.println("Tool list updated: " + updatedTools.size() + " tools available");
//        // Update local tool registry
//        toolRegistry.updateTools(updatedTools);
//    }
}
