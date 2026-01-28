package org.palladiosimulator.analyzer.slingshot.mcp.tools;

import java.util.concurrent.CompletableFuture;

import org.palladiosimulator.analyzer.slingshot.mcp.headless.StandaloneModelLoader.ModelLoadingException;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationResult;
import org.palladiosimulator.analyzer.slingshot.mcp.state.SimulationStateManager;

/**
 * MCP Tool: slingshot_run
 * Executes a configured simulation session.
 */
public class RunSimulationTool {

    private final SimulationStateManager stateManager;

    public RunSimulationTool(SimulationStateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Input parameters for the run tool.
     */
    public static class Input {
        private String sessionId;
        private boolean async = false;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public boolean isAsync() {
            return async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }
    }

    /**
     * Output for the run tool.
     */
    public static class Output {
        private String sessionId;
        private String status;
        private String message;
        private Double simulatedTime;
        private Long eventCount;
        private Integer measurementCount;
        private Long durationMs;

        public Output(String sessionId, String status, String message) {
            this.sessionId = sessionId;
            this.status = status;
            this.message = message;
        }

        public static Output fromResult(SimulationResult result) {
            Output output = new Output(
                    result.getSessionId(),
                    result.getStatus().toString().toLowerCase(),
                    result.getErrorMessage() != null ? result.getErrorMessage() : "Simulation completed"
            );
            output.simulatedTime = result.getSimulatedTime();
            output.eventCount = result.getEventCount();
            output.measurementCount = result.getMeasurementCount();
            if (result.getDuration() != null) {
                output.durationMs = result.getDuration().toMillis();
            }
            return output;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Double getSimulatedTime() {
            return simulatedTime;
        }

        public Long getEventCount() {
            return eventCount;
        }

        public Integer getMeasurementCount() {
            return measurementCount;
        }

        public Long getDurationMs() {
            return durationMs;
        }

        public String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"sessionId\": \"").append(sessionId).append("\",\n");
            sb.append("  \"status\": \"").append(status).append("\",\n");
            sb.append("  \"message\": \"").append(message != null ? message : "").append("\"");

            if (simulatedTime != null) {
                sb.append(",\n  \"simulatedTime\": ").append(simulatedTime);
            }
            if (eventCount != null) {
                sb.append(",\n  \"eventCount\": ").append(eventCount);
            }
            if (measurementCount != null) {
                sb.append(",\n  \"measurementCount\": ").append(measurementCount);
            }
            if (durationMs != null) {
                sb.append(",\n  \"durationMs\": ").append(durationMs);
            }

            sb.append("\n}");
            return sb.toString();
        }
    }

    /**
     * Execute the run tool synchronously.
     *
     * @param input The input parameters
     * @return The output with simulation results
     */
    public Output execute(Input input) {
        if (input.getSessionId() == null || input.getSessionId().isEmpty()) {
            return new Output(null, "error", "Session ID is required");
        }

        try {
            if (input.isAsync()) {
                // Start async and return immediately
                CompletableFuture<SimulationResult> future = stateManager.runSimulationAsync(input.getSessionId());
                return new Output(
                        input.getSessionId(),
                        "running",
                        "Simulation started asynchronously. Use slingshot_status to check progress."
                );
            } else {
                // Run synchronously and return full results
                SimulationResult result = stateManager.runSimulation(input.getSessionId());
                return Output.fromResult(result);
            }
        } catch (ModelLoadingException e) {
            return new Output(input.getSessionId(), "error", "Model loading failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return new Output(input.getSessionId(), "error", e.getMessage());
        } catch (Exception e) {
            return new Output(input.getSessionId(), "error", "Simulation failed: " + e.getMessage());
        }
    }

    /**
     * Get the tool name.
     */
    public static String getToolName() {
        return "slingshot_run";
    }

    /**
     * Get the tool description.
     */
    public static String getToolDescription() {
        return "Executes a configured simulation session. Can run synchronously (blocking) or " +
                "asynchronously (returns immediately). Use slingshot_status to check async progress.";
    }
}
