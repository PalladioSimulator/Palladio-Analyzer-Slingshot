package org.palladiosimulator.analyzer.slingshot.workflow;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.workflow.events.WorkflowLaunchConfigurationBuilderInitialized;
import org.palladiosimulator.analyzer.slingshot.workflow.jobs.SimulationRootJob;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;

import de.uka.ipd.sdq.workflow.jobs.IJob;

public class SimulationLauncher extends AbstractPCMLaunchConfigurationDelegate<SimulationWorkflowConfiguration> {

	private static final Logger LOGGER = Logger.getLogger(SimulationLauncher.class);
	
	private final SystemDriver systemDriver = Slingshot.getInstance().getSystemDriver();
	
	@Override
	protected IJob createWorkflowJob(SimulationWorkflowConfiguration config, ILaunch launch) throws CoreException {
		return new SimulationRootJob(config, launch);
	}

	@Override
	protected SimulationWorkflowConfiguration deriveConfiguration(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		final SimulationWorkflowConfiguration simulationWorkflowConfiguration = new SimulationWorkflowConfiguration();
		final WorkflowLaunchConfigurationBuilderInitialized builderEvent = new WorkflowLaunchConfigurationBuilderInitialized(configuration, simulationWorkflowConfiguration);
		systemDriver.postEvent(builderEvent);
		return simulationWorkflowConfiguration;
	}

}
