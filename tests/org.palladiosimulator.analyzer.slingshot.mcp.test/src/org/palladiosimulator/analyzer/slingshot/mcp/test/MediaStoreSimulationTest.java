package org.palladiosimulator.analyzer.slingshot.mcp.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.mcp.config.HeadlessSimulationConfig;
import org.palladiosimulator.analyzer.slingshot.mcp.config.ModelPaths;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationResult;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationSession;
import org.palladiosimulator.analyzer.slingshot.mcp.tools.ListModelsTool;

/**
 * Integration test for the MCP module using the MediaStore example model.
 */
class MediaStoreSimulationTest {

    private static final String MEDIASTORE_BASE = "test-models/Palladio-Example-Models/ScreencastMediaStore";

    private static Path modelsPath;

    @BeforeAll
    static void setUp() {
        // Find the project root directory
        Path currentDir = Paths.get(System.getProperty("user.dir"));

        // Navigate up to find the root with test-models
        while (currentDir != null && !Files.exists(currentDir.resolve(MEDIASTORE_BASE))) {
            currentDir = currentDir.getParent();
        }

        if (currentDir != null) {
            modelsPath = currentDir.resolve(MEDIASTORE_BASE);
        }
    }

    @Test
    void testListModelsTool() throws IOException {
        if (modelsPath == null || !Files.exists(modelsPath)) {
            System.out.println("Skipping test: MediaStore models not found at " + MEDIASTORE_BASE);
            return;
        }

        ListModelsTool tool = new ListModelsTool();
        ListModelsTool.Input input = new ListModelsTool.Input();
        input.setDirectory(modelsPath.toString());

        ListModelsTool.Output output = tool.execute(input);

        assertNotNull(output, "Output should not be null");
        assertNotNull(output.getModels(), "Models list should not be null");
        assertFalse(output.getModels().isEmpty(), "Should find some models");

        // Should find allocation, repository, system, usagemodel, resourceenvironment
        boolean foundAllocation = output.getModels().stream()
            .anyMatch(m -> m.getType().equalsIgnoreCase("Allocation"));
        boolean foundRepository = output.getModels().stream()
            .anyMatch(m -> m.getType().equalsIgnoreCase("Repository"));
        boolean foundSystem = output.getModels().stream()
            .anyMatch(m -> m.getType().equalsIgnoreCase("System"));

        assertTrue(foundAllocation, "Should find allocation model");
        assertTrue(foundRepository, "Should find repository model");
        assertTrue(foundSystem, "Should find system model");

        System.out.println("Found " + output.getModels().size() + " models:");
        output.getModels().forEach(m ->
            System.out.println("  - " + m.getName() + " (" + m.getType() + ")"));
    }

    @Test
    void testModelPathsConfiguration() {
        if (modelsPath == null || !Files.exists(modelsPath)) {
            System.out.println("Skipping test: MediaStore models not found");
            return;
        }

        ModelPaths paths = ModelPaths.builder()
            .allocation(modelsPath.resolve("MediaStore-Cacheless.allocation").toString())
            .usageModel(modelsPath.resolve("MediaStore.usagemodel").toString())
            .repository(modelsPath.resolve("MediaStore.repository").toString())
            .system(modelsPath.resolve("MediaStore-Cacheless.system").toString())
            .resourceEnvironment(modelsPath.resolve("MediaStore.resourceenvironment").toString())
            .build();

        assertNotNull(paths.getAllocationPath(), "Allocation path should be set");
        assertNotNull(paths.getUsageModelPath(), "Usage model path should be set");
        assertNotNull(paths.getRepositoryPath(), "Repository path should be set");
        assertNotNull(paths.getSystemPath(), "System path should be set");
        assertNotNull(paths.getResourceEnvironmentPath(), "Resource environment path should be set");

        assertTrue(Files.exists(Paths.get(paths.getAllocationPath())), "Allocation file should exist");
        assertTrue(Files.exists(Paths.get(paths.getUsageModelPath())), "Usage model file should exist");
        assertTrue(Files.exists(Paths.get(paths.getRepositoryPath())), "Repository file should exist");
        assertTrue(Files.exists(Paths.get(paths.getSystemPath())), "System file should exist");
        assertTrue(Files.exists(Paths.get(paths.getResourceEnvironmentPath())), "Resource environment file should exist");

        System.out.println("Model paths configured successfully:");
        System.out.println("  Allocation: " + paths.getAllocationPath());
        System.out.println("  Usage Model: " + paths.getUsageModelPath());
        System.out.println("  Repository: " + paths.getRepositoryPath());
        System.out.println("  System: " + paths.getSystemPath());
        System.out.println("  Resource Environment: " + paths.getResourceEnvironmentPath());
    }

    @Test
    void testHeadlessSimulationConfig() {
        if (modelsPath == null || !Files.exists(modelsPath)) {
            System.out.println("Skipping test: MediaStore models not found");
            return;
        }

        ModelPaths paths = ModelPaths.builder()
            .allocation(modelsPath.resolve("MediaStore-Cacheless.allocation").toString())
            .usageModel(modelsPath.resolve("MediaStore.usagemodel").toString())
            .repository(modelsPath.resolve("MediaStore.repository").toString())
            .system(modelsPath.resolve("MediaStore-Cacheless.system").toString())
            .resourceEnvironment(modelsPath.resolve("MediaStore.resourceenvironment").toString())
            .build();

        HeadlessSimulationConfig config = HeadlessSimulationConfig.builder()
            .modelPaths(paths)
            .simulationTime(100.0)
            .maxMeasurements(1000)
            .experimentName("MediaStore-Test")
            .build();

        assertNotNull(config, "Config should not be null");
        assertEquals(100.0, config.getSimulationTime(), "Simulation time should be 100.0");
        assertEquals(1000, config.getMaxMeasurements(), "Max measurements should be 1000");
        assertEquals("MediaStore-Test", config.getExperimentName(), "Experiment name should match");

        System.out.println("Simulation config created:");
        System.out.println("  Simulation time: " + config.getSimulationTime());
        System.out.println("  Max measurements: " + config.getMaxMeasurements());
        System.out.println("  Experiment name: " + config.getExperimentName());
    }

    @Test
    void testSimulationSessionCreation() {
        if (modelsPath == null || !Files.exists(modelsPath)) {
            System.out.println("Skipping test: MediaStore models not found");
            return;
        }

        ModelPaths paths = ModelPaths.builder()
            .allocation(modelsPath.resolve("MediaStore-Cacheless.allocation").toString())
            .usageModel(modelsPath.resolve("MediaStore.usagemodel").toString())
            .repository(modelsPath.resolve("MediaStore.repository").toString())
            .system(modelsPath.resolve("MediaStore-Cacheless.system").toString())
            .resourceEnvironment(modelsPath.resolve("MediaStore.resourceenvironment").toString())
            .build();

        HeadlessSimulationConfig config = HeadlessSimulationConfig.builder()
            .modelPaths(paths)
            .simulationTime(100.0)
            .build();

        SimulationSession session = new SimulationSession(config);

        assertNotNull(session, "Session should not be null");
        assertNotNull(session.getSessionId(), "Session ID should not be null");
        assertFalse(session.getSessionId().isEmpty(), "Session ID should not be empty");
        // Session is initialized as RUNNING
        assertEquals(SimulationResult.SimulationStatus.RUNNING, session.getStatus(),
            "Initial status should be RUNNING");

        System.out.println("Session created:");
        System.out.println("  Session ID: " + session.getSessionId());
        System.out.println("  Status: " + session.getStatus());
    }
}
