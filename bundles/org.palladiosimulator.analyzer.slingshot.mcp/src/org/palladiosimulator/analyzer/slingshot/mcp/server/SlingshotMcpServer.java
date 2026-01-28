package org.palladiosimulator.analyzer.slingshot.mcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.mcp.headless.HeadlessSlingshot;
import org.palladiosimulator.analyzer.slingshot.mcp.state.SimulationStateManager;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.ConfigureSimulationTool;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.GetResultsTool;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.GetStatusTool;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.ListModelsTool;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.RunSimulationTool;

/**
 * MCP Server for Slingshot Palladio Simulator.
 *
 * Implements the Model Context Protocol (MCP) using stdio transport.
 * This allows AI agents like Claude to interact with Slingshot simulations.
 *
 * Usage:
 *   java -jar slingshot-mcp-server.jar
 *
 * The server communicates via stdin/stdout using JSON-RPC 2.0 format.
 */
public class SlingshotMcpServer {

    private static final Logger LOGGER = Logger.getLogger(SlingshotMcpServer.class);

    private static final String SERVER_NAME = "slingshot-mcp";
    private static final String SERVER_VERSION = "1.0.0";
    private static final String PROTOCOL_VERSION = "2024-11-05";

    private final SimulationStateManager stateManager;
    private final ToolRegistry toolRegistry;
    private volatile boolean running;

    public SlingshotMcpServer() {
        // Initialize Slingshot first
        HeadlessSlingshot.getInstance();

        this.stateManager = new SimulationStateManager();
        this.toolRegistry = new ToolRegistry(stateManager);
        this.running = false;
    }

    /**
     * Start the MCP server with stdio transport.
     */
    public void start() throws IOException {
        LOGGER.info("Starting Slingshot MCP Server");
        running = true;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(System.out, true, StandardCharsets.UTF_8)) {

            while (running) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                try {
                    String response = handleRequest(line);
                    if (response != null) {
                        writer.println(response);
                    }
                } catch (Exception e) {
                    LOGGER.error("Error handling request", e);
                    String errorResponse = createErrorResponse(null, -32603, "Internal error: " + e.getMessage());
                    writer.println(errorResponse);
                }
            }
        }

        LOGGER.info("Slingshot MCP Server stopped");
    }

    /**
     * Stop the server.
     */
    public void stop() {
        running = false;
        stateManager.shutdown();
    }

    /**
     * Handle a JSON-RPC request.
     */
    private String handleRequest(String jsonRequest) {
        // Simple JSON parsing (for production, use a proper JSON library)
        String method = extractJsonString(jsonRequest, "method");
        String id = extractJsonValue(jsonRequest, "id");
        String params = extractJsonObject(jsonRequest, "params");

        if (method == null) {
            return createErrorResponse(id, -32600, "Invalid Request: missing method");
        }

        LOGGER.debug("Handling method: " + method);

        switch (method) {
            case "initialize":
                return handleInitialize(id);
            case "tools/list":
                return handleToolsList(id);
            case "tools/call":
                return handleToolsCall(id, params);
            case "shutdown":
                running = false;
                return createSuccessResponse(id, "{}");
            default:
                return createErrorResponse(id, -32601, "Method not found: " + method);
        }
    }

    private String handleInitialize(String id) {
        String capabilities = """
            {
              "protocolVersion": "%s",
              "serverInfo": {
                "name": "%s",
                "version": "%s"
              },
              "capabilities": {
                "tools": {}
              }
            }
            """.formatted(PROTOCOL_VERSION, SERVER_NAME, SERVER_VERSION);

        return createSuccessResponse(id, capabilities);
    }

    private String handleToolsList(String id) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"tools\": [\n");

        var tools = toolRegistry.getAllTools();
        int i = 0;
        for (var entry : tools.entrySet()) {
            var tool = entry.getValue();
            sb.append("    {\n");
            sb.append("      \"name\": \"").append(tool.getName()).append("\",\n");
            sb.append("      \"description\": \"").append(escapeJson(tool.getDescription())).append("\",\n");
            sb.append("      \"inputSchema\": ").append(tool.getInputSchema()).append("\n");
            sb.append("    }");
            if (i < tools.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
            i++;
        }

        sb.append("  ]\n}");
        return createSuccessResponse(id, sb.toString());
    }

    private String handleToolsCall(String id, String params) {
        String toolName = extractJsonString(params, "name");
        String arguments = extractJsonObject(params, "arguments");

        if (toolName == null) {
            return createErrorResponse(id, -32602, "Invalid params: missing tool name");
        }

        LOGGER.info("Calling tool: " + toolName);

        try {
            String result = executeTool(toolName, arguments);
            String response = """
                {
                  "content": [
                    {
                      "type": "text",
                      "text": %s
                    }
                  ]
                }
                """.formatted(escapeJsonString(result));
            return createSuccessResponse(id, response);
        } catch (Exception e) {
            LOGGER.error("Error executing tool: " + toolName, e);
            return createErrorResponse(id, -32000, "Tool execution error: " + e.getMessage());
        }
    }

    private String executeTool(String toolName, String arguments) throws Exception {
        switch (toolName) {
            case "slingshot_list_models": {
                ListModelsTool.Input input = new ListModelsTool.Input();
                input.setDirectory(extractJsonString(arguments, "directory"));
                input.setModelType(extractJsonString(arguments, "modelType"));
                return toolRegistry.getListModelsTool().execute(input).toJson();
            }
            case "slingshot_configure": {
                ConfigureSimulationTool.Input input = new ConfigureSimulationTool.Input();
                input.setSessionId(extractJsonString(arguments, "sessionId"));

                String modelsJson = extractJsonObject(arguments, "models");
                if (modelsJson != null) {
                    ConfigureSimulationTool.ModelPathsInput models = new ConfigureSimulationTool.ModelPathsInput();
                    models.setAllocation(extractJsonString(modelsJson, "allocation"));
                    models.setUsageModel(extractJsonString(modelsJson, "usageModel"));
                    models.setRepository(extractJsonString(modelsJson, "repository"));
                    models.setSystem(extractJsonString(modelsJson, "system"));
                    models.setResourceEnvironment(extractJsonString(modelsJson, "resourceEnvironment"));
                    models.setMonitorRepository(extractJsonString(modelsJson, "monitorRepository"));
                    models.setMeasuringPoint(extractJsonString(modelsJson, "measuringPoint"));
                    models.setSlo(extractJsonString(modelsJson, "slo"));
                    models.setSpd(extractJsonString(modelsJson, "spd"));
                    input.setModels(models);
                }

                String simTime = extractJsonValue(arguments, "simulationTime");
                if (simTime != null) {
                    input.setSimulationTime(Double.parseDouble(simTime));
                }

                String seed = extractJsonValue(arguments, "seed");
                if (seed != null) {
                    input.setSeed(Long.parseLong(seed));
                }

                String maxMeas = extractJsonValue(arguments, "maxMeasurements");
                if (maxMeas != null) {
                    input.setMaxMeasurements(Integer.parseInt(maxMeas));
                }

                input.setExperimentName(extractJsonString(arguments, "experimentName"));
                return toolRegistry.getConfigureTool().execute(input).toJson();
            }
            case "slingshot_run": {
                RunSimulationTool.Input input = new RunSimulationTool.Input();
                input.setSessionId(extractJsonString(arguments, "sessionId"));
                String async = extractJsonValue(arguments, "async");
                if (async != null) {
                    input.setAsync(Boolean.parseBoolean(async));
                }
                return toolRegistry.getRunTool().execute(input).toJson();
            }
            case "slingshot_status": {
                GetStatusTool.Input input = new GetStatusTool.Input();
                input.setSessionId(extractJsonString(arguments, "sessionId"));
                return toolRegistry.getStatusTool().execute(input).toJson();
            }
            case "slingshot_results": {
                GetResultsTool.Input input = new GetResultsTool.Input();
                input.setSessionId(extractJsonString(arguments, "sessionId"));
                input.setMetric(extractJsonString(arguments, "metric"));
                input.setFormat(extractJsonString(arguments, "format"));

                String limit = extractJsonValue(arguments, "limit");
                if (limit != null) {
                    input.setLimit(Integer.parseInt(limit));
                }

                String offset = extractJsonValue(arguments, "offset");
                if (offset != null) {
                    input.setOffset(Integer.parseInt(offset));
                }

                GetResultsTool.Output output = toolRegistry.getResultsTool().execute(input);
                return "csv".equals(input.getFormat()) ? output.toCsv() : output.toJson();
            }
            default:
                throw new IllegalArgumentException("Unknown tool: " + toolName);
        }
    }

    // Simple JSON utilities (for production, use a proper JSON library)

    private String extractJsonString(String json, String key) {
        if (json == null) return null;
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractJsonValue(String json, String key) {
        if (json == null) return null;
        String pattern = "\"" + key + "\"\\s*:\\s*([^,}\\]]+)";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(json);
        if (matcher.find()) {
            String value = matcher.group(1).trim();
            if (value.startsWith("\"") && value.endsWith("\"")) {
                return value.substring(1, value.length() - 1);
            }
            return value;
        }
        return null;
    }

    private String extractJsonObject(String json, String key) {
        if (json == null) return null;
        int start = json.indexOf("\"" + key + "\"");
        if (start < 0) return null;

        int colonPos = json.indexOf(":", start);
        if (colonPos < 0) return null;

        int braceStart = json.indexOf("{", colonPos);
        if (braceStart < 0) return null;

        int depth = 1;
        int pos = braceStart + 1;
        while (pos < json.length() && depth > 0) {
            char c = json.charAt(pos);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            pos++;
        }

        return json.substring(braceStart, pos);
    }

    private String createSuccessResponse(String id, String result) {
        return """
            {"jsonrpc": "2.0", "id": %s, "result": %s}
            """.formatted(id != null ? id : "null", result).trim();
    }

    private String createErrorResponse(String id, int code, String message) {
        return """
            {"jsonrpc": "2.0", "id": %s, "error": {"code": %d, "message": "%s"}}
            """.formatted(id != null ? id : "null", code, escapeJson(message)).trim();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String escapeJsonString(String s) {
        return "\"" + escapeJson(s) + "\"";
    }

    /**
     * Main entry point for the MCP server.
     */
    public static void main(String[] args) {
        try {
            SlingshotMcpServer server = new SlingshotMcpServer();
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start MCP server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
