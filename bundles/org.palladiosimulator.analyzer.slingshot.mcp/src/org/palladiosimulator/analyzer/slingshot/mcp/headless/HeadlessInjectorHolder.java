package org.palladiosimulator.analyzer.slingshot.mcp.headless;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.core.annotations.SimulationBehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.annotations.SystemBehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorContainer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * Creates and holds a Guice injector for headless (non-Eclipse) execution.
 * This replaces the InjectorHolder that depends on Eclipse plugin activation.
 */
public class HeadlessInjectorHolder {

    private static final Logger LOGGER = Logger.getLogger(HeadlessInjectorHolder.class);

    private final Injector injector;
    private final List<AbstractSlingshotExtension> extensions;

    /**
     * Create an injector holder with all default extensions.
     */
    public HeadlessInjectorHolder() {
        this(ProgrammaticExtensionRegistry.createExtensions());
    }

    /**
     * Create an injector holder with the given extensions.
     *
     * @param extensions The extension modules to use
     */
    public HeadlessInjectorHolder(List<AbstractSlingshotExtension> extensions) {
        this.extensions = extensions;

        List<Module> modules = new ArrayList<>(extensions);
        modules.add(new HeadlessSlingshotModule(extensions));

        LOGGER.info("Creating Guice injector with " + modules.size() + " modules");
        modules.forEach(module -> LOGGER.debug("Module: " + module.getClass().getName()));

        this.injector = Guice.createInjector(modules);
    }

    public <T> T getInstance(Class<T> clazz) {
        return this.injector.getInstance(clazz);
    }

    public <T> T getInstance(Key<T> key) {
        return this.injector.getInstance(key);
    }

    public <T> Provider<T> getProvider(Class<T> clazz) {
        return this.injector.getProvider(clazz);
    }

    public Injector getInjector() {
        return this.injector;
    }

    public List<AbstractSlingshotExtension> getExtensions() {
        return extensions;
    }

    /**
     * Headless version of SlingshotModule that doesn't depend on Slingshot.getInstance().
     */
    private static class HeadlessSlingshotModule extends AbstractModule {

        private final Logger LOGGER = Logger.getLogger(HeadlessSlingshotModule.class);

        private final List<SystemBehaviorContainer> systemContainers;
        private final List<SimulationBehaviorContainer> simulationContainers;

        public HeadlessSlingshotModule(List<AbstractSlingshotExtension> extensions) {
            this.systemContainers = extensions.stream()
                    .map(extension -> new SystemBehaviorContainer(extension))
                    .collect(Collectors.toList());
            this.simulationContainers = extensions.stream()
                    .map(SimulationBehaviorContainer::new)
                    .collect(Collectors.toList());

            LOGGER.debug("Created " + systemContainers.size() + " system behavior containers");
            LOGGER.debug("Created " + simulationContainers.size() + " simulation behavior containers");
        }

        @Override
        protected void configure() {
            bind(PCMResourceSetPartitionProvider.class);
            // SystemDriver binding will come from the core module or we bind it dynamically
            try {
                Class<?> systemDriverImplClass = Class.forName(
                    "org.palladiosimulator.analyzer.slingshot.core.driver.SlingshotSystemDriver");
                bind(SystemDriver.class).to((Class<? extends SystemDriver>) systemDriverImplClass);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Could not find SlingshotSystemDriver class", e);
            }
        }

        @Singleton
        @Provides
        @SystemBehaviorExtensions
        public List<SystemBehaviorContainer> getSystemBehaviorContainers() {
            return this.systemContainers;
        }

        @Singleton
        @Provides
        @SimulationBehaviorExtensions
        public List<SimulationBehaviorContainer> getSimulationBehaviorContainer() {
            return this.simulationContainers;
        }
    }
}
