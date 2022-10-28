package org.palladiosimulator.analyzer.slingshot.workflow;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SimulationWorkflowPlugin implements BundleActivator {

	private static SimulationWorkflowPlugin instance = null;
	
	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
	}

	public static SimulationWorkflowPlugin getInstance() {
		return instance;
	}
}
