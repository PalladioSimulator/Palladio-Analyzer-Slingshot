package org.palladiosimulator.analyzer.slingshot.mcp.runner;

import java.time.Instant;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.slingshot.mcp.config.HeadlessSimulationConfig;
import org.palladiosimulator.analyzer.slingshot.mcp.config.SimuComConfigFactory;
import org.palladiosimulator.analyzer.slingshot.mcp.headless.HeadlessSlingshot;
import org.palladiosimulator.analyzer.slingshot.mcp.headless.NullProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.mcp.headless.StandaloneModelLoader;
import org.palladiosimulator.analyzer.slingshot.mcp.headless.StandaloneModelLoader.ModelLoadingException;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationResult.SimulationStatus;
import org.palladiosimulator.analyzer.slingshot.workflow.WorkflowConfigurationModule;
import org.palladiosimulator.analyzer.workflow.core.blackboard.PCMResourceSetPartition;

import de.uka.ipd.sdq.simucomframework.core.SimuComConfig;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

/**
 * Main API for running Slingshot simulations programmatically.
 *
 * Example usage:
 * <pre>{@code
 * ModelPaths paths = ModelPaths.builder()
 *     .allocation("/path/to/model.allocation")
 *     .usageModel("/path/to/model.usagemodel")
 *     .build();
 *
 * HeadlessSimulationConfig config = HeadlessSimulationConfig.builder()
 *     .modelPaths(paths)
 *     .simulationTime(1000.0)
 *     .build();
 *
 * SlingshotRunner runner = new SlingshotRunner();
 * SimulationResult result = runner.runSimulation(config);
 * }</pre>
 */
public class SlingshotRunner {

    private static final Logger LOGGER = Logger.getLogger(SlingshotRunner.class);

    private final HeadlessSlingshot slingshot;

    /**
     * Create a new runner with default extensions.
     */
    public SlingshotRunner() {
        this.slingshot = HeadlessSlingshot.getInstance();
    }

    /**
     * Create a runner with a custom HeadlessSlingshot instance.
     *
     * @param slingshot The HeadlessSlingshot instance to use
     */
    public SlingshotRunner(HeadlessSlingshot slingshot) {
        this.slingshot = slingshot;
    }

    /**
     * Run a simulation with the given configuration.
     *
     * @param config The simulation configuration
     * @return The simulation result containing all measurements
     * @throws ModelLoadingException If models cannot be loaded
     */
    public SimulationResult runSimulation(HeadlessSimulationConfig config) throws ModelLoadingException {
        LOGGER.info("Starting simulation with config: " + config);

        SimulationSession session = new SimulationSession(config);
        session.markStarted();

        try {
            // Load PCM models
            StandaloneModelLoader modelLoader = new StandaloneModelLoader(config.getModelPaths());
            ResourceSet resourceSet = modelLoader.loadModels();

            // Create SimuComConfig
            SimuComConfig simuComConfig = SimuComConfigFactory.create(config);

            // Create PCMResourceSetPartition and set up providers
            PCMResourceSetPartition partition = new PCMResourceSetPartition();
            partition.initialiseResourceSetEPackages(
                new org.eclipse.emf.ecore.EPackage[] { org.palladiosimulator.pcm.PcmPackage.eINSTANCE }
            );

            // Copy resources from our ResourceSet to the partition
            for (org.eclipse.emf.ecore.resource.Resource resource : resourceSet.getResources()) {
                partition.getResourceSet().getResources().add(resource);
            }

            PCMResourceSetPartitionProvider partitionProvider = slingshot.getInstance(PCMResourceSetPartitionProvider.class);
            partitionProvider.set(partition);

            WorkflowConfigurationModule.simuComConfigProvider.set(simuComConfig);

            // Create a minimal blackboard for compatibility
            MDSDBlackboard blackboard = new MDSDBlackboard();
            blackboard.addPartition("org.palladiosimulator.pcmmodels.partition", partition);
            WorkflowConfigurationModule.blackboardProvider.set(blackboard);

            // Get simulation driver
            SimulationDriver driver = slingshot.getSimulationDriver();
            session.setDriver(driver);

            // Initialize and run
            LOGGER.info("Initializing simulation driver");
            driver.init(simuComConfig, new NullProgressMonitor());

            LOGGER.info("Starting simulation");
            driver.start();

            // Simulation runs synchronously, so when start() returns, it's done
            session.setCurrentSimulationTime(simuComConfig.getSimuTime());
            session.markCompleted();

            LOGGER.info("Simulation completed. Collected " + session.getMeasurementCount() + " measurements");

        } catch (ModelLoadingException e) {
            LOGGER.error("Failed to load models", e);
            session.markFailed("Model loading error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Simulation failed", e);
            session.markFailed("Simulation error: " + e.getMessage());
        }

        return session.toResult();
    }

    /**
     * Create a new simulation session without running it.
     * Useful for configuring and running simulations asynchronously.
     *
     * @param config The simulation configuration
     * @return A new session that can be started later
     */
    public SimulationSession createSession(HeadlessSimulationConfig config) {
        return new SimulationSession(config);
    }

    /**
     * Create a simulation session with a specific ID.
     *
     * @param sessionId The session identifier
     * @param config The simulation configuration
     * @return A new session with the given ID
     */
    public SimulationSession createSession(String sessionId, HeadlessSimulationConfig config) {
        return new SimulationSession(sessionId, config);
    }
}
