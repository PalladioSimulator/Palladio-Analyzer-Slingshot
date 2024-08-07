package org.palladiosimulator.analyzer.slingshot.core;

import java.util.Collections;
import java.util.List;

import javax.inject.Provider;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
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
	private List<AbstractSlingshotExtension> extensions = null;

	private InjectorHolder injectionHolder;

	static {
		setupLoggingLevel();
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
		this.extensions = null;
		this.injectionHolder = null;
		LOGGER.debug("Slingshot ended");
		super.stop(context);
	}

	public List<AbstractSlingshotExtension> getExtensions() {
		if (this.extensions == null) {
			this.extensions = ExtensionHelper.getExecutableExtensions(ExtensionIds.EXTENSION_POINT_ID,
					ExtensionIds.EXTENSION_ATTRIBUTE_NAME);
		}

		return Collections.unmodifiableList(this.extensions);
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

	/**
	 * Default log level is DEBUG. To provide another log level, specify a
	 * properties file and pass it as VM argument, e.g. as
	 * {@code -Dlog4j.configuration=file:///absolute/path/to/file/log4j.properties}.
	 *
	 */
	private static void setupLoggingLevel() {
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.removeAllAppenders();
		final Layout layout = new PatternLayout("%n\tat %C.%M(%F:%L)%n\t%-5p %d [%t] - %m%n");
		final Appender app = new ConsoleAppender(layout);
		rootLogger.addAppender(app);
	}
}
