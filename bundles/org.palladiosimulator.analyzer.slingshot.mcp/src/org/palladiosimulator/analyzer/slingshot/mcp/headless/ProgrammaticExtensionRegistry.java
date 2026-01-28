package org.palladiosimulator.analyzer.slingshot.mcp.headless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

/**
 * Programmatic registry of Slingshot extension modules for headless execution.
 * This replaces the Eclipse extension point mechanism for standalone usage.
 *
 * All AbstractSlingshotExtension implementations that would normally be
 * registered via plugin.xml are loaded via reflection here.
 */
public final class ProgrammaticExtensionRegistry {

    private static final Logger LOGGER = Logger.getLogger(ProgrammaticExtensionRegistry.class);

    /**
     * List of all extension module class names in the order they should be loaded.
     * Note: WorkflowConfigurationModule should be first as it provides core bindings.
     */
    private static final List<String> EXTENSION_CLASS_NAMES;

    static {
        List<String> classNames = new ArrayList<>();

        // Core workflow configuration (must be first)
        classNames.add("org.palladiosimulator.analyzer.slingshot.workflow.WorkflowConfigurationModule");

        // General simulation configuration
        classNames.add("org.palladiosimulator.analyzer.slingshot.behavior.generalsimulationconfiguration.GeneralConfigurationBehaviorModule");

        // Monitoring infrastructure
        classNames.add("org.palladiosimulator.analyzer.slingshot.monitor.MonitorModule");
        classNames.add("org.palladiosimulator.analyzer.slingshot.monitor.processingtype.feedthrough.FeedThroughProcessingTypeModule");
        classNames.add("org.palladiosimulator.analyzer.slingshot.monitor.processingtype.aggregator.ProcessingTypeAggregationModule");

        // Resource simulation
        classNames.add("org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.ResourceSimulationModule");
        classNames.add("org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor.ResourceSimulationMonitorModule");

        // System simulation
        classNames.add("org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.SystemSimulatorModule");
        classNames.add("org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor.SystemModelMonitorModule");

        // Usage simulation
        classNames.add("org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.UsageSimulationModule");
        classNames.add("org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.monitor.UsageModelMonitorModule");
        classNames.add("org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.UsageEvolutionModule");

        // SPD (Scaling Policy Definition) modules
        classNames.add("org.palladiosimulator.analyzer.slingshot.behavior.spd.interpreter.SPDInterpreterModule");
        classNames.add("org.palladiosimulator.analyzer.slingshot.behavior.spd.adjustment.SpdAdjustorModule");

        EXTENSION_CLASS_NAMES = Collections.unmodifiableList(classNames);
    }

    private ProgrammaticExtensionRegistry() {
        // Utility class
    }

    /**
     * Get all extension class names.
     *
     * @return Unmodifiable list of extension module class names
     */
    public static List<String> getExtensionClassNames() {
        return EXTENSION_CLASS_NAMES;
    }

    /**
     * Create instances of all extension modules using reflection.
     *
     * @return List of instantiated extension modules
     */
    @SuppressWarnings("unchecked")
    public static List<AbstractSlingshotExtension> createExtensions() {
        List<AbstractSlingshotExtension> extensions = new ArrayList<>();

        for (String className : EXTENSION_CLASS_NAMES) {
            try {
                Class<?> clazz = Class.forName(className);
                if (AbstractSlingshotExtension.class.isAssignableFrom(clazz)) {
                    AbstractSlingshotExtension extension =
                        (AbstractSlingshotExtension) clazz.getDeclaredConstructor().newInstance();
                    extensions.add(extension);
                    LOGGER.debug("Created extension: " + className);
                } else {
                    LOGGER.warn("Class does not extend AbstractSlingshotExtension: " + className);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Extension class not found (may be optional): " + className);
            } catch (Exception e) {
                LOGGER.error("Failed to create extension: " + className, e);
            }
        }

        LOGGER.info("Created " + extensions.size() + " extension modules");
        return extensions;
    }

    /**
     * Create instances of a subset of extension modules by class name.
     *
     * @param classNames The extension class names to instantiate
     * @return List of instantiated extension modules
     */
    @SuppressWarnings("unchecked")
    public static List<AbstractSlingshotExtension> createExtensions(List<String> classNames) {
        List<AbstractSlingshotExtension> extensions = new ArrayList<>();

        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (AbstractSlingshotExtension.class.isAssignableFrom(clazz)) {
                    AbstractSlingshotExtension extension =
                        (AbstractSlingshotExtension) clazz.getDeclaredConstructor().newInstance();
                    extensions.add(extension);
                    LOGGER.debug("Created extension: " + className);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to create extension: " + className, e);
            }
        }

        return extensions;
    }

    /**
     * Get the number of registered extension class names.
     *
     * @return Number of extension class names
     */
    public static int getExtensionCount() {
        return EXTENSION_CLASS_NAMES.size();
    }
}
