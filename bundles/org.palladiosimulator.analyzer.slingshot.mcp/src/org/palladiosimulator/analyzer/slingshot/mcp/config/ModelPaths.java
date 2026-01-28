package org.palladiosimulator.analyzer.slingshot.mcp.config;

/**
 * Configuration holder for PCM model file paths.
 * Use the builder pattern to construct instances.
 */
public class ModelPaths {

    private final String allocationPath;
    private final String usageModelPath;
    private final String repositoryPath;
    private final String systemPath;
    private final String resourceEnvironmentPath;
    private final String monitorRepositoryPath;
    private final String measuringPointPath;
    private final String sloPath;
    private final String spdPath;

    private ModelPaths(Builder builder) {
        this.allocationPath = builder.allocationPath;
        this.usageModelPath = builder.usageModelPath;
        this.repositoryPath = builder.repositoryPath;
        this.systemPath = builder.systemPath;
        this.resourceEnvironmentPath = builder.resourceEnvironmentPath;
        this.monitorRepositoryPath = builder.monitorRepositoryPath;
        this.measuringPointPath = builder.measuringPointPath;
        this.sloPath = builder.sloPath;
        this.spdPath = builder.spdPath;
    }

    public String getAllocationPath() {
        return allocationPath;
    }

    public String getUsageModelPath() {
        return usageModelPath;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public String getSystemPath() {
        return systemPath;
    }

    public String getResourceEnvironmentPath() {
        return resourceEnvironmentPath;
    }

    public String getMonitorRepositoryPath() {
        return monitorRepositoryPath;
    }

    public String getMeasuringPointPath() {
        return measuringPointPath;
    }

    public String getSloPath() {
        return sloPath;
    }

    public String getSpdPath() {
        return spdPath;
    }

    @Override
    public String toString() {
        return "ModelPaths{" +
                "allocation='" + allocationPath + '\'' +
                ", usageModel='" + usageModelPath + '\'' +
                ", repository='" + repositoryPath + '\'' +
                ", system='" + systemPath + '\'' +
                ", resourceEnvironment='" + resourceEnvironmentPath + '\'' +
                ", monitorRepository='" + monitorRepositoryPath + '\'' +
                ", measuringPoint='" + measuringPointPath + '\'' +
                ", slo='" + sloPath + '\'' +
                ", spd='" + spdPath + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String allocationPath;
        private String usageModelPath;
        private String repositoryPath;
        private String systemPath;
        private String resourceEnvironmentPath;
        private String monitorRepositoryPath;
        private String measuringPointPath;
        private String sloPath;
        private String spdPath;

        public Builder allocation(String path) {
            this.allocationPath = path;
            return this;
        }

        public Builder usageModel(String path) {
            this.usageModelPath = path;
            return this;
        }

        public Builder repository(String path) {
            this.repositoryPath = path;
            return this;
        }

        public Builder system(String path) {
            this.systemPath = path;
            return this;
        }

        public Builder resourceEnvironment(String path) {
            this.resourceEnvironmentPath = path;
            return this;
        }

        public Builder monitorRepository(String path) {
            this.monitorRepositoryPath = path;
            return this;
        }

        public Builder measuringPoint(String path) {
            this.measuringPointPath = path;
            return this;
        }

        public Builder slo(String path) {
            this.sloPath = path;
            return this;
        }

        public Builder spd(String path) {
            this.spdPath = path;
            return this;
        }

        public ModelPaths build() {
            return new ModelPaths(this);
        }
    }
}
