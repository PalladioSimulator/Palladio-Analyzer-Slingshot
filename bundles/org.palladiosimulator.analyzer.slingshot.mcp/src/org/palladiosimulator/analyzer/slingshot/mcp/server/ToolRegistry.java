package org.palladiosimulator.analyzer.slingshot.mcp.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.palladiosimulator.analyzer.slingshot.mcp.state.SimulationStateManager;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.ConfigureSimulationTool;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.GetResultsTool;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.GetStatusTool;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.ListModelsTool;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.RunSimulationTool;

/**
 * Registry of all available MCP tools.
 * Provides tool metadata and execution handlers.
 */
public class ToolRegistry {

    private final Map<String, ToolDefinition> tools;
    private final SimulationStateManager stateManager;

    // Tool instances
    private final ListModelsTool listModelsTool;
    private final ConfigureSimulationTool configureTool;
    private final RunSimulationTool runTool;
    private final GetStatusTool statusTool;
    private final GetResultsTool resultsTool;

    public ToolRegistry(SimulationStateManager stateManager) {
        this.stateManager = stateManager;
        this.tools = new HashMap<>();

        // Create tool instances
        this.listModelsTool = new ListModelsTool();
        this.configureTool = new ConfigureSimulationTool(stateManager);
        this.runTool = new RunSimulationTool(stateManager);
        this.statusTool = new GetStatusTool(stateManager);
        this.resultsTool = new GetResultsTool(stateManager);

        // Register tools
        registerTools();
    }

    private void registerTools() {
        // slingshot_list_models
        tools.put(ListModelsTool.getToolName(), new ToolDefinition(
                ListModelsTool.getToolName(),
                ListModelsTool.getToolDescription(),
                getListModelsSchema()
        ));

        // slingshot_configure
        tools.put(ConfigureSimulationTool.getToolName(), new ToolDefinition(
                ConfigureSimulationTool.getToolName(),
                ConfigureSimulationTool.getToolDescription(),
                getConfigureSchema()
        ));

        // slingshot_run
        tools.put(RunSimulationTool.getToolName(), new ToolDefinition(
                RunSimulationTool.getToolName(),
                RunSimulationTool.getToolDescription(),
                getRunSchema()
        ));

        // slingshot_status
        tools.put(GetStatusTool.getToolName(), new ToolDefinition(
                GetStatusTool.getToolName(),
                GetStatusTool.getToolDescription(),
                getStatusSchema()
        ));

        // slingshot_results
        tools.put(GetResultsTool.getToolName(), new ToolDefinition(
                GetResultsTool.getToolName(),
                GetResultsTool.getToolDescription(),
                getResultsSchema()
        ));
    }

    public Set<String> getToolNames() {
        return tools.keySet();
    }

    public ToolDefinition getTool(String name) {
        return tools.get(name);
    }

    public Map<String, ToolDefinition> getAllTools() {
        return new HashMap<>(tools);
    }

    public ListModelsTool getListModelsTool() {
        return listModelsTool;
    }

    public ConfigureSimulationTool getConfigureTool() {
        return configureTool;
    }

    public RunSimulationTool getRunTool() {
        return runTool;
    }

    public GetStatusTool getStatusTool() {
        return statusTool;
    }

    public GetResultsTool getResultsTool() {
        return resultsTool;
    }

    // Schema definitions (JSON Schema format)

    private String getListModelsSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "directory": {
                  "type": "string",
                  "description": "The directory path to scan for PCM model files"
                },
                "modelType": {
                  "type": "string",
                  "description": "Optional filter by model type (Repository, System, Allocation, etc.)"
                }
              },
              "required": ["directory"]
            }
            """;
    }

    private String getConfigureSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "sessionId": {
                  "type": "string",
                  "description": "Optional custom session ID. If not provided, one will be generated."
                },
                "models": {
                  "type": "object",
                  "description": "Paths to PCM model files",
                  "properties": {
                    "allocation": {"type": "string", "description": "Path to allocation model"},
                    "usageModel": {"type": "string", "description": "Path to usage model"},
                    "repository": {"type": "string", "description": "Path to repository model"},
                    "system": {"type": "string", "description": "Path to system model"},
                    "resourceEnvironment": {"type": "string", "description": "Path to resource environment model"},
                    "monitorRepository": {"type": "string", "description": "Path to monitor repository"},
                    "measuringPoint": {"type": "string", "description": "Path to measuring points"},
                    "slo": {"type": "string", "description": "Path to SLO model"},
                    "spd": {"type": "string", "description": "Path to SPD model"}
                  },
                  "required": ["allocation", "usageModel"]
                },
                "simulationTime": {
                  "type": "number",
                  "description": "Maximum simulation time in simulation time units",
                  "default": 1000
                },
                "seed": {
                  "type": "integer",
                  "description": "Random number generator seed for reproducibility"
                },
                "maxMeasurements": {
                  "type": "integer",
                  "description": "Maximum number of measurements to collect",
                  "default": 10000
                },
                "experimentName": {
                  "type": "string",
                  "description": "Name for this experiment run"
                }
              },
              "required": ["models"]
            }
            """;
    }

    private String getRunSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "sessionId": {
                  "type": "string",
                  "description": "The session ID returned from slingshot_configure"
                },
                "async": {
                  "type": "boolean",
                  "description": "If true, run asynchronously and return immediately",
                  "default": false
                }
              },
              "required": ["sessionId"]
            }
            """;
    }

    private String getStatusSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "sessionId": {
                  "type": "string",
                  "description": "The session ID to check status for"
                }
              },
              "required": ["sessionId"]
            }
            """;
    }

    private String getResultsSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "sessionId": {
                  "type": "string",
                  "description": "The session ID to get results from"
                },
                "metric": {
                  "type": "string",
                  "description": "Optional filter by metric name (partial match)"
                },
                "format": {
                  "type": "string",
                  "enum": ["json", "csv"],
                  "description": "Output format",
                  "default": "json"
                },
                "limit": {
                  "type": "integer",
                  "description": "Maximum number of measurements to return",
                  "default": 100
                },
                "offset": {
                  "type": "integer",
                  "description": "Offset for pagination",
                  "default": 0
                }
              },
              "required": ["sessionId"]
            }
            """;
    }

    /**
     * Tool definition containing name, description, and input schema.
     */
    public static class ToolDefinition {
        private final String name;
        private final String description;
        private final String inputSchema;

        public ToolDefinition(String name, String description, String inputSchema) {
            this.name = name;
            this.description = description;
            this.inputSchema = inputSchema;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getInputSchema() {
            return inputSchema;
        }
    }
}
