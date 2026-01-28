package org.palladiosimulator.analyzer.slingshot.mcp.config;

import java.util.HashMap;
import java.util.Map;

import de.uka.ipd.sdq.simucomframework.core.SimuComConfig;

/**
 * Factory for creating SimuComConfig instances from HeadlessSimulationConfig.
 */
public final class SimuComConfigFactory {

    // SimuComConfig attribute keys (from SimuComConfig constants)
    private static final String SIMULATION_TIME = "SIMULATION_TIME";
    private static final String MAXIMUM_MEASUREMENT_COUNT = "MAXIMUM_MEASUREMENT_COUNT";
    private static final String RANDOM_NUMBER_GENERATOR_SEED = "RANDOM_NUMBER_GENERATOR_SEED";
    private static final String EXPERIMENT_RUN = "EXPERIMENT_RUN";
    private static final String VARIATION_ID = "VARIATION_ID";
    private static final String VERBOSE_LOGGING = "VERBOSE_LOGGING";
    private static final String SIMULATE_FAILURES = "SIMULATE_FAILURES";
    private static final String SIMULATE_LINKING_RESOURCES = "SIMULATE_LINKING_RESOURCES";

    private SimuComConfigFactory() {
        // Utility class
    }

    /**
     * Create a SimuComConfig from a HeadlessSimulationConfig.
     *
     * @param config The headless configuration
     * @return A SimuComConfig suitable for simulation
     */
    public static SimuComConfig create(HeadlessSimulationConfig config) {
        Map<String, Object> attributes = new HashMap<>();

        // Core simulation parameters
        attributes.put(SIMULATION_TIME, String.valueOf(config.getSimulationTime()));
        attributes.put(MAXIMUM_MEASUREMENT_COUNT, String.valueOf(config.getMaxMeasurements()));
        attributes.put(RANDOM_NUMBER_GENERATOR_SEED, String.valueOf(config.getRandomSeed()));
        attributes.put(EXPERIMENT_RUN, config.getExperimentName());
        attributes.put(VARIATION_ID, "default");
        attributes.put(VERBOSE_LOGGING, config.isVerboseLogging());
        attributes.put(SIMULATE_FAILURES, false);
        attributes.put(SIMULATE_LINKING_RESOURCES, true);

        return new SimuComConfig(attributes, false);
    }

    /**
     * Create a SimuComConfig with custom attributes.
     *
     * @param baseConfig The base headless configuration
     * @param additionalAttributes Additional attributes to merge
     * @return A SimuComConfig with merged attributes
     */
    public static SimuComConfig create(HeadlessSimulationConfig baseConfig,
                                       Map<String, Object> additionalAttributes) {
        Map<String, Object> attributes = new HashMap<>();

        // Add base configuration
        attributes.put(SIMULATION_TIME, String.valueOf(baseConfig.getSimulationTime()));
        attributes.put(MAXIMUM_MEASUREMENT_COUNT, String.valueOf(baseConfig.getMaxMeasurements()));
        attributes.put(RANDOM_NUMBER_GENERATOR_SEED, String.valueOf(baseConfig.getRandomSeed()));
        attributes.put(EXPERIMENT_RUN, baseConfig.getExperimentName());
        attributes.put(VARIATION_ID, "default");
        attributes.put(VERBOSE_LOGGING, baseConfig.isVerboseLogging());
        attributes.put(SIMULATE_FAILURES, false);
        attributes.put(SIMULATE_LINKING_RESOURCES, true);

        // Merge additional attributes
        attributes.putAll(additionalAttributes);

        return new SimuComConfig(attributes, false);
    }
}
