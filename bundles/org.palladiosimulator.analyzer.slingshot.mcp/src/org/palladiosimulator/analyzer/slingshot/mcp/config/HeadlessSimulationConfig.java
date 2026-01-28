package org.palladiosimulator.analyzer.slingshot.mcp.config;

/**
 * Configuration holder for headless simulation runs.
 * Use the builder pattern to construct instances.
 */
public class HeadlessSimulationConfig {

    private final ModelPaths modelPaths;
    private final double simulationTime;
    private final long randomSeed;
    private final int maxMeasurements;
    private final String experimentName;
    private final boolean verboseLogging;

    private HeadlessSimulationConfig(Builder builder) {
        this.modelPaths = builder.modelPaths;
        this.simulationTime = builder.simulationTime;
        this.randomSeed = builder.randomSeed;
        this.maxMeasurements = builder.maxMeasurements;
        this.experimentName = builder.experimentName;
        this.verboseLogging = builder.verboseLogging;
    }

    public ModelPaths getModelPaths() {
        return modelPaths;
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public int getMaxMeasurements() {
        return maxMeasurements;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public boolean isVerboseLogging() {
        return verboseLogging;
    }

    @Override
    public String toString() {
        return "HeadlessSimulationConfig{" +
                "modelPaths=" + modelPaths +
                ", simulationTime=" + simulationTime +
                ", randomSeed=" + randomSeed +
                ", maxMeasurements=" + maxMeasurements +
                ", experimentName='" + experimentName + '\'' +
                ", verboseLogging=" + verboseLogging +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ModelPaths modelPaths;
        private double simulationTime = 1000.0;
        private long randomSeed = 42L;
        private int maxMeasurements = 10000;
        private String experimentName = "HeadlessSimulation";
        private boolean verboseLogging = false;

        public Builder modelPaths(ModelPaths paths) {
            this.modelPaths = paths;
            return this;
        }

        public Builder simulationTime(double time) {
            this.simulationTime = time;
            return this;
        }

        public Builder randomSeed(long seed) {
            this.randomSeed = seed;
            return this;
        }

        public Builder maxMeasurements(int max) {
            this.maxMeasurements = max;
            return this;
        }

        public Builder experimentName(String name) {
            this.experimentName = name;
            return this;
        }

        public Builder verboseLogging(boolean verbose) {
            this.verboseLogging = verbose;
            return this;
        }

        public HeadlessSimulationConfig build() {
            if (modelPaths == null) {
                throw new IllegalStateException("Model paths must be specified");
            }
            return new HeadlessSimulationConfig(this);
        }
    }
}
