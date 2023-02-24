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

public class Slingshot extends Plugin {

	private static final Logger LOGGER = LogManager.getLogger(Slingshot.class);

	public static final String BUNDLE_ID = "";

	private static Slingshot bundle = null;
	private List<AbstractSlingshotExtension> extensions = null;

	private InjectorHolder slingshotModule;

	static {
		setupLoggingLevelToDebug();
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		bundle = this;
		this.slingshotModule = new InjectorHolder();
		LOGGER.debug("Slingshot started");
		super.start(context);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		bundle = null;
		this.extensions = null;
		this.slingshotModule = null;
		LOGGER.debug("Slingshot ended");
		super.stop(context);
	}

	public List<AbstractSlingshotExtension> getExtensions() {
		if (this.extensions == null) {
			this.extensions = ExtensionHelper.getExecutableExtensions(ExtensionIds.EXTENSION_POINT_ID, ExtensionIds.EXTENSION_ATTRIBUTE_NAME);
		}

		return Collections.unmodifiableList(this.extensions);
	}


	public static Slingshot getInstance() {
		return bundle;
	}

	public SystemDriver getSystemDriver() {
		return slingshotModule.getInstance(SystemDriver.class); // TODO
	}

	public SimulationDriver getSimulationDriver() {
		return slingshotModule.getInstance(SimulationDriver.class);
	}

	public <T> T getInstance(final Class<T> clazz) {
		return this.slingshotModule.getInstance(clazz);
	}

	public <T> Provider<T> getProvider(final Class<T> clazz) {
		return this.slingshotModule.getProvider(clazz);
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
