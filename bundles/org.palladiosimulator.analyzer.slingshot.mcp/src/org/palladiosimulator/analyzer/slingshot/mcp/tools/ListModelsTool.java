package org.palladiosimulator.analyzer.slingshot.mcp.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MCP Tool: slingshot_list_models
 * Scans a directory for PCM model files and returns metadata about them.
 */
public class ListModelsTool {

    private static final Map<String, String> EXTENSION_TO_TYPE = new HashMap<>();

    static {
        EXTENSION_TO_TYPE.put("repository", "Repository");
        EXTENSION_TO_TYPE.put("system", "System");
        EXTENSION_TO_TYPE.put("allocation", "Allocation");
        EXTENSION_TO_TYPE.put("resourceenvironment", "ResourceEnvironment");
        EXTENSION_TO_TYPE.put("usagemodel", "UsageModel");
        EXTENSION_TO_TYPE.put("monitorrepository", "MonitorRepository");
        EXTENSION_TO_TYPE.put("measuringpoint", "MeasuringPoint");
        EXTENSION_TO_TYPE.put("slo", "ServiceLevelObjective");
        EXTENSION_TO_TYPE.put("spd", "ScalingPolicyDefinition");
    }

    /**
     * Input parameters for the list_models tool.
     */
    public static class Input {
        private String directory;
        private String modelType; // Optional filter

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        public String getModelType() {
            return modelType;
        }

        public void setModelType(String modelType) {
            this.modelType = modelType;
        }
    }

    /**
     * Output for a single model file.
     */
    public static class ModelInfo {
        private String path;
        private String name;
        private String type;
        private long size;
        private String lastModified;

        public ModelInfo(String path, String name, String type, long size, String lastModified) {
            this.path = path;
            this.name = name;
            this.type = type;
            this.size = size;
            this.lastModified = lastModified;
        }

        public String getPath() {
            return path;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public long getSize() {
            return size;
        }

        public String getLastModified() {
            return lastModified;
        }

        @Override
        public String toString() {
            return String.format("%s (%s) - %s", name, type, path);
        }
    }

    /**
     * Output for the list_models tool.
     */
    public static class Output {
        private List<ModelInfo> models;
        private int totalCount;
        private String directory;

        public Output(List<ModelInfo> models, String directory) {
            this.models = models;
            this.totalCount = models.size();
            this.directory = directory;
        }

        public List<ModelInfo> getModels() {
            return models;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public String getDirectory() {
            return directory;
        }

        public String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"directory\": \"").append(escapeJson(directory)).append("\",\n");
            sb.append("  \"totalCount\": ").append(totalCount).append(",\n");
            sb.append("  \"models\": [\n");

            for (int i = 0; i < models.size(); i++) {
                ModelInfo model = models.get(i);
                sb.append("    {\n");
                sb.append("      \"path\": \"").append(escapeJson(model.getPath())).append("\",\n");
                sb.append("      \"name\": \"").append(escapeJson(model.getName())).append("\",\n");
                sb.append("      \"type\": \"").append(escapeJson(model.getType())).append("\",\n");
                sb.append("      \"size\": ").append(model.getSize()).append(",\n");
                sb.append("      \"lastModified\": \"").append(escapeJson(model.getLastModified())).append("\"\n");
                sb.append("    }");
                if (i < models.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }

            sb.append("  ]\n");
            sb.append("}");
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
     * Execute the list_models tool.
     *
     * @param input The input parameters
     * @return The output with found models
     * @throws IOException If directory cannot be read
     */
    public Output execute(Input input) throws IOException {
        File dir = new File(input.getDirectory());
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Directory does not exist: " + input.getDirectory());
        }

        List<ModelInfo> models = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(dir.toPath())) {
            List<Path> files = paths.filter(Files::isRegularFile).collect(Collectors.toList());

            for (Path path : files) {
                String fileName = path.getFileName().toString();
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    String extension = fileName.substring(dotIndex + 1).toLowerCase();
                    String type = EXTENSION_TO_TYPE.get(extension);

                    if (type != null) {
                        // Apply filter if specified
                        if (input.getModelType() == null || type.equalsIgnoreCase(input.getModelType())) {
                            File file = path.toFile();
                            models.add(new ModelInfo(
                                    file.getAbsolutePath(),
                                    fileName,
                                    type,
                                    file.length(),
                                    java.time.Instant.ofEpochMilli(file.lastModified()).toString()
                            ));
                        }
                    }
                }
            }
        }

        return new Output(models, dir.getAbsolutePath());
    }

    /**
     * Get the tool name.
     */
    public static String getToolName() {
        return "slingshot_list_models";
    }

    /**
     * Get the tool description.
     */
    public static String getToolDescription() {
        return "Scans a directory for PCM model files and returns metadata about them. " +
                "Supports filtering by model type (Repository, System, Allocation, etc.).";
    }
}
