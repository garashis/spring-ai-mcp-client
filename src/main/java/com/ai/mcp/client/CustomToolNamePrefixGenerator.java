package com.ai.mcp.client;

import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.McpConnectionInfo;
import org.springframework.ai.mcp.McpToolNamePrefixGenerator;
import org.springframework.stereotype.Component;

@Component
public class CustomToolNamePrefixGenerator implements McpToolNamePrefixGenerator {

    @Override
    public String prefixedToolName(McpConnectionInfo connectionInfo, McpSchema.Tool tool) {
        // Custom logic to generate prefixed tool names

        // Example: Use server name and version as prefix
        String serverName = connectionInfo.initializeResult().serverInfo().name();
        String serverVersion = connectionInfo.initializeResult().serverInfo().version();
        return serverName + "_v" + serverVersion.replace(".", "_") + "_" + tool.name();
    }

}