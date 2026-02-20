package com.ai.mcp.client;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springaicommunity.mcp.annotation.McpProgress;
import org.springaicommunity.mcp.annotation.McpSampling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.modelcontextprotocol.spec.McpSchema.*;

@Component
@RequiredArgsConstructor
public class McpClientHandlers {
    private List<McpAsyncClient> mcpAsyncClients;

    public McpClientHandlers(List<McpAsyncClient> mcpAsyncClients){
        System.out.println("handleLoggingMessage const");
        mcpAsyncClients.forEach(mcpAsyncClient -> mcpAsyncClient.setLoggingLevel(McpSchema.LoggingLevel.DEBUG).block());
    }

    @McpLogging(clients = {"server1"})
    public Mono<Void> handleLoggingMessageMono(LoggingMessageNotification notification) {
        System.out.println("handleLoggingMessage log: " + notification.level() +
                " - " + notification.data());
        return Mono.empty();
    }

    //@McpLogging(clients = {"my-mcp-client", "server1", "my-mcp-client - server1", "my-mcp-client-server1"})
    public void handleLoggingMessageVoid(LoggingMessageNotification notification) {
        System.out.println("handleLoggingMessageVoid log: " + notification.level() +
                " - " + notification.data());
    }

    @McpLogging(clients = {"my-mcp-client", "server1", "my-mcp-client - server1", "my-mcp-client-server1"})
    public Mono<Void>  handleLoggingMessageWithParams(LoggingLevel level, String logger, String data) {
        System.out.println("handleLoggingMessageWithParams log: " + data);
        return Mono.empty();

    }
    

    //@McpSampling(clients = "my-mcp-client")
    public CreateMessageResult handleSamplingRequest(CreateMessageRequest request) {
        // Process the request and generate a response
        String response = "This is the response from server";

        return CreateMessageResult.builder()
                .role(Role.ASSISTANT)
                .content(new TextContent(response))
                .model("gpt-4")
                .build();
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
