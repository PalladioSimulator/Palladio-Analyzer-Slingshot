package org.palladiosimulator.analyzer.slingshot.debugger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		EventDebugSystem.initializeEventHolder();
		EventDebugSystem.initializeEventTree();
	}

	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
