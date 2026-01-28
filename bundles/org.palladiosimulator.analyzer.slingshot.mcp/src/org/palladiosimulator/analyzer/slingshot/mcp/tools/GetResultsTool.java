package org.palladiosimulator.analyzer.slingshot.mcp.tools;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationResult.MeasurementRecord;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationSession;
import org.palladiosimulator.analyzer.slingshot.mcp.state.SimulationStateManager;

/**
 * MCP Tool: slingshot_results
 * Retrieves measurements from a completed simulation session.
 */
public class GetResultsTool {

    private final SimulationStateManager stateManager;

    public GetResultsTool(SimulationStateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Input parameters for the results tool.
     */
    public static class Input {
        private String sessionId;
        private String metric; // Optional filter by metric name
        private String format = "json"; // json or csv
        private int limit = 100; // Max results to return
        private int offset = 0; // Pagination offset

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }

    /**
     * Output for the results tool.
     */
    public static class Output {
        private String sessionId;
        private String status;
        private int totalMeasurements;
        private int returnedMeasurements;
        private int offset;
        private boolean hasMore;
        private List<MeasurementRecord> measurements;
        private String errorMessage;

        public Output(String sessionId, String status) {
            this.sessionId = sessionId;
            this.status = status;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getStatus() {
            return status;
        }

        public int getTotalMeasurements() {
            return totalMeasurements;
        }

        public void setTotalMeasurements(int totalMeasurements) {
            this.totalMeasurements = totalMeasurements;
        }

        public int getReturnedMeasurements() {
            return returnedMeasurements;
        }

        public void setReturnedMeasurements(int returnedMeasurements) {
            this.returnedMeasurements = returnedMeasurements;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public boolean isHasMore() {
            return hasMore;
        }

        public void setHasMore(boolean hasMore) {
            this.hasMore = hasMore;
        }

        public List<MeasurementRecord> getMeasurements() {
            return measurements;
        }

        public void setMeasurements(List<MeasurementRecord> measurements) {
            this.measurements = measurements;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"sessionId\": \"").append(sessionId).append("\",\n");
            sb.append("  \"status\": \"").append(status).append("\",\n");
            sb.append("  \"totalMeasurements\": ").append(totalMeasurements).append(",\n");
            sb.append("  \"returnedMeasurements\": ").append(returnedMeasurements).append(",\n");
            sb.append("  \"offset\": ").append(offset).append(",\n");
            sb.append("  \"hasMore\": ").append(hasMore);

            if (errorMessage != null) {
                sb.append(",\n  \"errorMessage\": \"").append(escapeJson(errorMessage)).append("\"");
            }

            if (measurements != null && !measurements.isEmpty()) {
                sb.append(",\n  \"measurements\": [\n");
                for (int i = 0; i < measurements.size(); i++) {
                    MeasurementRecord m = measurements.get(i);
                    sb.append("    {\n");
                    sb.append("      \"simulationTime\": ").append(m.getSimulationTime()).append(",\n");
                    sb.append("      \"measuringPoint\": \"").append(escapeJson(m.getMeasuringPointName())).append("\",\n");
                    sb.append("      \"metric\": \"").append(escapeJson(m.getMetricName())).append("\",\n");
                    sb.append("      \"measures\": [");
                    for (int j = 0; j < m.getMeasures().size(); j++) {
                        sb.append("\"").append(m.getMeasures().get(j).toString()).append("\"");
                        if (j < m.getMeasures().size() - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append("]\n");
                    sb.append("    }");
                    if (i < measurements.size() - 1) {
                        sb.append(",");
                    }
                    sb.append("\n");
                }
                sb.append("  ]");
            }

            sb.append("\n}");
            return sb.toString();
        }

        public String toCsv() {
            StringBuilder sb = new StringBuilder();
            sb.append("simulationTime,measuringPoint,metric,value\n");

            if (measurements != null) {
                for (MeasurementRecord m : measurements) {
                    for (var measure : m.getMeasures()) {
                        sb.append(m.getSimulationTime()).append(",");
                        sb.append(escapeCsv(m.getMeasuringPointName())).append(",");
                        sb.append(escapeCsv(m.getMetricName())).append(",");
                        sb.append(escapeCsv(measure.toString())).append("\n");
                    }
                }
            }

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

        private String escapeCsv(String s) {
            if (s == null) return "";
            if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
                return "\"" + s.replace("\"", "\"\"") + "\"";
            }
            return s;
        }
    }

    /**
     * Execute the results tool.
     *
     * @param input The input parameters
     * @return The output with measurements
     */
    public Output execute(Input input) {
        if (input.getSessionId() == null || input.getSessionId().isEmpty()) {
            Output output = new Output(null, "error");
            output.setErrorMessage("Session ID is required");
            return output;
        }

        Optional<SimulationSession> sessionOpt = stateManager.getSession(input.getSessionId());
        if (sessionOpt.isEmpty()) {
            Output output = new Output(input.getSessionId(), "not_found");
            output.setErrorMessage("Session not found");
            return output;
        }

        SimulationSession session = sessionOpt.get();
        List<MeasurementRecord> allMeasurements = session.getMeasurements();

        // Filter by metric if specified
        if (input.getMetric() != null && !input.getMetric().isEmpty()) {
            String metricFilter = input.getMetric().toLowerCase();
            allMeasurements = allMeasurements.stream()
                    .filter(m -> m.getMetricName().toLowerCase().contains(metricFilter))
                    .collect(Collectors.toList());
        }

        int total = allMeasurements.size();
        int offset = Math.min(input.getOffset(), total);
        int limit = input.getLimit();
        int endIndex = Math.min(offset + limit, total);

        List<MeasurementRecord> paged = allMeasurements.subList(offset, endIndex);

        Output output = new Output(input.getSessionId(), session.getStatus().toString().toLowerCase());
        output.setTotalMeasurements(total);
        output.setReturnedMeasurements(paged.size());
        output.setOffset(offset);
        output.setHasMore(endIndex < total);
        output.setMeasurements(paged);

        return output;
    }

    /**
     * Get the tool name.
     */
    public static String getToolName() {
        return "slingshot_results";
    }

    /**
     * Get the tool description.
     */
    public static String getToolDescription() {
        return "Retrieves measurements from a simulation session. Supports filtering by metric name, " +
                "pagination (limit/offset), and output format (json/csv).";
    }
}
