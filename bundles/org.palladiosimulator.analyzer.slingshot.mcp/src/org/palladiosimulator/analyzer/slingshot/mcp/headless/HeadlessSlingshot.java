package org.palladiosimulator.analyzer.slingshot.mcp.headless;

import java.util.List;

import javax.inject.Provider;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.palladiosimulator.analyzer.slingshot.core.SimulationModule;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.analyzer.slingshot.mcp.util.EMFStandaloneSetup;

import com.google.inject.Injector;

/**
 * Headless entry point for Slingshot simulation.
 * This replaces the Eclipse Plugin-based Slingshot for standalone execution.
 *
 * Unlike the original Slingshot class, this does not extend Eclipse Plugin
 * and can be used without OSGi/Eclipse runtime.
 */
public class HeadlessSlingshot {

    private static final Logger LOGGER = Logger.getLogger(HeadlessSlingshot.class);

    private static HeadlessSlingshot instance = null;

    private final HeadlessInjectorHolder injectorHolder;

    static {
        setupLoggingLevel();
    }

    /**
     * Create a new HeadlessSlingshot with all default extensions.
     */
    public HeadlessSlingshot() {
        this(ProgrammaticExtensionRegistry.createExtensions());
    }

    /**
     * Create a new HeadlessSlingshot with the given extensions.
     *
     * @param extensions The extension modules to use
     */
    public HeadlessSlingshot(List<AbstractSlingshotExtension> extensions) {
        // Ensure EMF is initialized for standalone usage
        EMFStandaloneSetup.init();

        LOGGER.info("Initializing HeadlessSlingshot with " + extensions.size() + " extensions");
        this.injectorHolder = new HeadlessInjectorHolder(extensions);
        LOGGER.info("HeadlessSlingshot initialized successfully");
    }

    /**
     * Get or create the singleton instance.
     * Creates a new instance with default extensions if not already initialized.
     *
     * @return The HeadlessSlingshot instance
     */
    public static synchronized HeadlessSlingshot getInstance() {
        if (instance == null) {
            instance = new HeadlessSlingshot();
        }
        return instance;
    }

    /**
     * Initialize the singleton instance with custom extensions.
     * This must be called before getInstance() if custom extensions are needed.
     *
     * @param extensions The extension modules to use
     * @return The initialized HeadlessSlingshot instance
     */
    public static synchronized HeadlessSlingshot initialize(List<AbstractSlingshotExtension> extensions) {
        if (instance != null) {
            LOGGER.warn("HeadlessSlingshot already initialized, reinitializing with new extensions");
        }
        instance = new HeadlessSlingshot(extensions);
        return instance;
    }

    /**
     * Reset the singleton instance (for testing purposes).
     */
    public static synchronized void reset() {
        instance = null;
    }

    /**
     * Get the system driver for event dispatching.
     *
     * @return The SystemDriver instance
     */
    public SystemDriver getSystemDriver() {
        return injectorHolder.getInstance(SystemDriver.class);
    }

    /**
     * Create a new simulation driver for a simulation run.
     * Each simulation run should create its own driver.
     *
     * @return A new SimulationDriver instance
     */
    public SimulationDriver getSimulationDriver() {
        final Injector parent = this.injectorHolder.getInjector();
        final Injector child = parent.createChildInjector(List.of(new SimulationModule()));
        return child.getInstance(SimulationDriver.class);
    }

    /**
     * Get an instance of a class from the injector.
     *
     * @param clazz The class to get an instance of
     * @param <T> The type of the class
     * @return An instance of the class
     */
    public <T> T getInstance(Class<T> clazz) {
        return this.injectorHolder.getInstance(clazz);
    }

    /**
     * Get a provider for a class from the injector.
     *
     * @param clazz The class to get a provider for
     * @param <T> The type of the class
     * @return A provider for the class
     */
    public <T> Provider<T> getProvider(Class<T> clazz) {
        return this.injectorHolder.getProvider(clazz);
    }

    /**
     * Get the list of loaded extensions.
     *
     * @return The list of extension modules
     */
    public List<AbstractSlingshotExtension> getExtensions() {
        return this.injectorHolder.getExtensions();
    }

    /**
     * Get the underlying injector holder.
     *
     * @return The HeadlessInjectorHolder
     */
    public HeadlessInjectorHolder getInjectorHolder() {
        return this.injectorHolder;
    }

    /**
     * Set up logging for standalone execution.
     */
    private static void setupLoggingLevel() {
        final Logger rootLogger = Logger.getRootLogger();
        if (!rootLogger.getAllAppenders().hasMoreElements()) {
            final Layout layout = new PatternLayout("%n\tat %C.%M(%F:%L)%n\t%-5p %d [%t] - %m%n");
            final Appender app = new ConsoleAppender(layout);
            rootLogger.addAppender(app);
        }
    }
}
