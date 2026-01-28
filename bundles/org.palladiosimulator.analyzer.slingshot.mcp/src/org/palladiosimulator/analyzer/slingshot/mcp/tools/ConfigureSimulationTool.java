package org.palladiosimulator.analyzer.slingshot.mcp.tools;

import org.palladiosimulator.analyzer.slingshot.mcp.config.HeadlessSimulationConfig;
import org.palladiosimulator.analyzer.slingshot.mcp.config.ModelPaths;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationSession;
import org.palladiosimulator.analyzer.slingshot.mcp.state.SimulationStateManager;

/**
 * MCP Tool: slingshot_configure
 * Creates a new simulation session with the specified configuration.
 */
public class ConfigureSimulationTool {

    private final SimulationStateManager stateManager;

    public ConfigureSimulationTool(SimulationStateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Input parameters for the configure tool.
     */
    public static class Input {
        private String sessionId;
        private ModelPathsInput models;
        private double simulationTime = 1000.0;
        private Long seed;
        private int maxMeasurements = 10000;
        private String experimentName;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public ModelPathsInput getModels() {
            return models;
        }

        public void setModels(ModelPathsInput models) {
            this.models = models;
        }

        public double getSimulationTime() {
            return simulationTime;
        }

        public void setSimulationTime(double simulationTime) {
            this.simulationTime = simulationTime;
        }

        public Long getSeed() {
            return seed;
        }

        public void setSeed(Long seed) {
            this.seed = seed;
        }

        public int getMaxMeasurements() {
            return maxMeasurements;
        }

        public void setMaxMeasurements(int maxMeasurements) {
            this.maxMeasurements = maxMeasurements;
        }

        public String getExperimentName() {
            return experimentName;
        }

        public void setExperimentName(String experimentName) {
            this.experimentName = experimentName;
        }
    }

    /**
     * Model paths input structure.
     */
    public static class ModelPathsInput {
        private String allocation;
        private String usageModel;
        private String repository;
        private String system;
        private String resourceEnvironment;
        private String monitorRepository;
        private String measuringPoint;
        private String slo;
        private String spd;

        public String getAllocation() {
            return allocation;
        }

        public void setAllocation(String allocation) {
            this.allocation = allocation;
        }

        public String getUsageModel() {
            return usageModel;
        }

        public void setUsageModel(String usageModel) {
            this.usageModel = usageModel;
        }

        public String getRepository() {
            return repository;
        }

        public void setRepository(String repository) {
            this.repository = repository;
        }

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public String getResourceEnvironment() {
            return resourceEnvironment;
        }

        public void setResourceEnvironment(String resourceEnvironment) {
            this.resourceEnvironment = resourceEnvironment;
        }

        public String getMonitorRepository() {
            return monitorRepository;
        }

        public void setMonitorRepository(String monitorRepository) {
            this.monitorRepository = monitorRepository;
        }

        public String getMeasuringPoint() {
            return measuringPoint;
        }

        public void setMeasuringPoint(String measuringPoint) {
            this.measuringPoint = measuringPoint;
        }

        public String getSlo() {
            return slo;
        }

        public void setSlo(String slo) {
            this.slo = slo;
        }

        public String getSpd() {
            return spd;
        }

        public void setSpd(String spd) {
            this.spd = spd;
        }

        public ModelPaths toModelPaths() {
            return ModelPaths.builder()
                    .allocation(allocation)
                    .usageModel(usageModel)
                    .repository(repository)
                    .system(system)
                    .resourceEnvironment(resourceEnvironment)
                    .monitorRepository(monitorRepository)
                    .measuringPoint(measuringPoint)
                    .slo(slo)
                    .spd(spd)
                    .build();
        }
    }

    /**
     * Output for the configure tool.
     */
    public static class Output {
        private String sessionId;
        private String status;
        private String message;
        private double simulationTime;

        public Output(String sessionId, String status, String message, double simulationTime) {
            this.sessionId = sessionId;
            this.status = status;
            this.message = message;
            this.simulationTime = simulationTime;
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

        public double getSimulationTime() {
            return simulationTime;
        }

        public String toJson() {
            return String.format(
                    "{\n  \"sessionId\": \"%s\",\n  \"status\": \"%s\",\n  \"message\": \"%s\",\n  \"simulationTime\": %f\n}",
                    sessionId, status, message, simulationTime
            );
        }
    }

    /**
     * Execute the configure tool.
     *
     * @param input The input parameters
     * @return The output with session information
     */
    public Output execute(Input input) {
        if (input.getModels() == null) {
            return new Output(null, "error", "Model paths are required", 0);
        }

        ModelPaths modelPaths = input.getModels().toModelPaths();

        HeadlessSimulationConfig.Builder configBuilder = HeadlessSimulationConfig.builder()
                .modelPaths(modelPaths)
                .simulationTime(input.getSimulationTime())
                .maxMeasurements(input.getMaxMeasurements());

        if (input.getSeed() != null) {
            configBuilder.randomSeed(input.getSeed());
        }

        if (input.getExperimentName() != null) {
            configBuilder.experimentName(input.getExperimentName());
        }

        HeadlessSimulationConfig config = configBuilder.build();

        try {
            SimulationSession session;
            if (input.getSessionId() != null && !input.getSessionId().isEmpty()) {
                session = stateManager.createSession(input.getSessionId(), config);
            } else {
                session = stateManager.createSession(config);
            }

            return new Output(
                    session.getSessionId(),
                    "configured",
                    "Session created successfully. Ready to run simulation.",
                    input.getSimulationTime()
            );
        } catch (IllegalArgumentException e) {
            return new Output(input.getSessionId(), "error", e.getMessage(), 0);
        }
    }

    /**
     * Get the tool name.
     */
    public static String getToolName() {
        return "slingshot_configure";
    }

    /**
     * Get the tool description.
     */
    public static String getToolDescription() {
        return "Creates a new simulation session with the specified PCM models and configuration. " +
                "Returns a session ID that can be used with other slingshot tools.";
    }
}
