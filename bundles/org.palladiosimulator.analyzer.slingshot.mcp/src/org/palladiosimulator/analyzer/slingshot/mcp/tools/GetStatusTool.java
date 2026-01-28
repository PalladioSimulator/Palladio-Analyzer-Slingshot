package org.palladiosimulator.analyzer.slingshot.mcp.tools;

import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationSession;
import org.palladiosimulator.analyzer.slingshot.mcp.state.SimulationStateManager;

/**
 * MCP Tool: slingshot_status
 * Gets the status of a simulation session.
 */
public class GetStatusTool {

    private final SimulationStateManager stateManager;

    public GetStatusTool(SimulationStateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Input parameters for the status tool.
     */
    public static class Input {
        private String sessionId;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    /**
     * Output for the status tool.
     */
    public static class Output {
        private String sessionId;
        private String status;
        private double simulationTime;
        private long eventCount;
        private int measurementCount;
        private String createdAt;
        private String startedAt;
        private String completedAt;
        private String errorMessage;

        public Output(String sessionId, String status) {
            this.sessionId = sessionId;
            this.status = status;
        }

        public static Output fromSession(SimulationSession session) {
            Output output = new Output(session.getSessionId(), session.getStatus().toString().toLowerCase());
            output.simulationTime = session.getCurrentSimulationTime();
            output.eventCount = session.getEventCount();
            output.measurementCount = session.getMeasurementCount();
            output.createdAt = session.getCreatedAt().toString();
            if (session.getStartedAt() != null) {
                output.startedAt = session.getStartedAt().toString();
            }
            if (session.getCompletedAt() != null) {
                output.completedAt = session.getCompletedAt().toString();
            }
            if (session.getErrorMessage() != null) {
                output.errorMessage = session.getErrorMessage();
            }
            return output;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getStatus() {
            return status;
        }

        public double getSimulationTime() {
            return simulationTime;
        }

        public long getEventCount() {
            return eventCount;
        }

        public int getMeasurementCount() {
            return measurementCount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getStartedAt() {
            return startedAt;
        }

        public String getCompletedAt() {
            return completedAt;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"sessionId\": \"").append(sessionId).append("\",\n");
            sb.append("  \"status\": \"").append(status).append("\",\n");
            sb.append("  \"simulationTime\": ").append(simulationTime).append(",\n");
            sb.append("  \"eventCount\": ").append(eventCount).append(",\n");
            sb.append("  \"measurementCount\": ").append(measurementCount);

            if (createdAt != null) {
                sb.append(",\n  \"createdAt\": \"").append(createdAt).append("\"");
            }
            if (startedAt != null) {
                sb.append(",\n  \"startedAt\": \"").append(startedAt).append("\"");
            }
            if (completedAt != null) {
                sb.append(",\n  \"completedAt\": \"").append(completedAt).append("\"");
            }
            if (errorMessage != null) {
                sb.append(",\n  \"errorMessage\": \"").append(escapeJson(errorMessage)).append("\"");
            }

            sb.append("\n}");
            return sb.toString();
        }

        private String escapeJson(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }
    }

    /**
     * Execute the status tool.
     *
     * @param input The input parameters
     * @return The output with session status
     */
    public Output execute(Input input) {
        if (input.getSessionId() == null || input.getSessionId().isEmpty()) {
            return new Output(null, "error");
        }

        Optional<SimulationSession> session = stateManager.getSession(input.getSessionId());
        if (session.isEmpty()) {
            Output output = new Output(input.getSessionId(), "not_found");
            return output;
        }

        return Output.fromSession(session.get());
    }

    /**
     * Get the tool name.
     */
    public static String getToolName() {
        return "slingshot_status";
    }

    /**
     * Get the tool description.
     */
    public static String getToolDescription() {
        return "Gets the current status of a simulation session including progress, " +
                "event count, and measurement count.";
    }
}
