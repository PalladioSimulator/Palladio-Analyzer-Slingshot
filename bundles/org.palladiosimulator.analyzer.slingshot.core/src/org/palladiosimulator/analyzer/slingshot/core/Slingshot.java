package org.palladiosimulator.analyzer.slingshot.core;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.driver.SlingshotSimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.engine.SimulationEngineSSJ;
import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.ExtensionIds;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;
import org.palladiosimulator.commons.eclipseutils.ExtensionHelper;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Slingshot extends Plugin {
	
	private static Slingshot bundle = null;
	
	private List<AbstractSlingshotExtension> extensions;
	
	private Injector guiceInjector;
	private final Bus bus = Bus.instance();

	@Override
	public void start(BundleContext context) throws Exception {
		bundle = this;
		
		this.extensions = ExtensionHelper.getExecutableExtensions(ExtensionIds.EXTENSION_POINT_ID, ExtensionIds.EXTENSION_ATTRIBUTE_NAME);
		
		this.guiceInjector = Guice.createInjector(new AbstractModule() {
			
			@Override
			protected void configure() {
				bind(SimulationDriver.class).to(SlingshotSimulationDriver.class);
				bind(SimulationEngine.class).to(SimulationEngineSSJ.class);
			}
			
		});
		
		
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		bundle = null;
		this.extensions = null;
		this.guiceInjector = null;
		super.stop(context);
	}
	
	public List<AbstractSlingshotExtension> getExtensions() {
		return Collections.unmodifiableList(this.extensions);
	}

	public static Slingshot getInstance() {
		return bundle;
	}
}
