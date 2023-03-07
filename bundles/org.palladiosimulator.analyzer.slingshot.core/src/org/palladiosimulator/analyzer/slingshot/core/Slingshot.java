package org.palladiosimulator.analyzer.slingshot.core;

import java.util.Collections;
import java.util.List;
import javax.inject.Provider;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.ExtensionIds;
import org.palladiosimulator.commons.eclipseutils.ExtensionHelper;

import com.google.inject.Injector;

public class Slingshot extends Plugin {

	private static final Logger LOGGER = LogManager.getLogger(Slingshot.class);

	public static final String BUNDLE_ID = "";

	private static Slingshot bundle = null;
	private List<AbstractSlingshotExtension> simulationExtensions = null;
	private List<AbstractSlingshotExtension> systemExtensions = null;

	private InjectorHolder injectionHolder;

	static {
		setupLoggingLevelToDebug();
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		bundle = this;
		this.injectionHolder = new InjectorHolder();
		LOGGER.debug("Slingshot started");
		super.start(context);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		bundle = null;
		this.simulationExtensions = null;
		this.systemExtensions = null;
		this.injectionHolder = null;
		LOGGER.debug("Slingshot ended");
		super.stop(context);
	}

	public List<AbstractSlingshotExtension> getSystemExtensions() {
		if (this.systemExtensions == null) {
			this.systemExtensions = ExtensionHelper.getExecutableExtensions(ExtensionIds.SYSTEM_EXTENSION_POINT_ID, ExtensionIds.SYSTEM_EXTENSION_ATTRIBUTE_NAME);
		}
		return Collections.unmodifiableList(systemExtensions);
	}

	public List<AbstractSlingshotExtension> getSimulationExtensions() {
		if (this.simulationExtensions == null) {
			this.simulationExtensions = ExtensionHelper.getExecutableExtensions(ExtensionIds.EXTENSION_POINT_ID, ExtensionIds.EXTENSION_ATTRIBUTE_NAME);
		}
		return Collections.unmodifiableList(simulationExtensions);
	}

	public static Slingshot getInstance() {
		return bundle;
	}

	public SystemDriver getSystemDriver() {
		return injectionHolder.getInstance(SystemDriver.class); // TODO
	}

	public SimulationDriver getSimulationDriver() {
		final Injector parent = this.injectionHolder.getInstance(Injector.class);
		final Injector child = parent.createChildInjector(List.of(new SimulationModule()));

		return child.getInstance(SimulationDriver.class);
	}

	public <T> T getInstance(final Class<T> clazz) {
		return this.injectionHolder.getInstance(clazz);
	}

	public <T> Provider<T> getProvider(final Class<T> clazz) {
		return this.injectionHolder.getProvider(clazz);
	}

	private static void setupLoggingLevelToDebug() {
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.removeAllAppenders();
		final Layout layout = new PatternLayout("%n\tat %C.%M(%F:%L)%n\t%-5p %d [%t] - %m%n");
		final Appender app = new ConsoleAppender(layout);
		rootLogger.addAppender(app);
		rootLogger.setLevel(Level.DEBUG);
	}
}
