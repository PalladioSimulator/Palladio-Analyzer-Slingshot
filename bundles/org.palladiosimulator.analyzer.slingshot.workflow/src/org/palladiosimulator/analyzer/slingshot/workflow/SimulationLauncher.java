package org.palladiosimulator.analyzer.slingshot.workflow;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.workflow.events.WorkflowLaunchConfigurationBuilderInitialized;
import org.palladiosimulator.analyzer.slingshot.workflow.jobs.SimulationRootJob;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;
import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.logging.console.LoggerAppenderStruct;

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
		final SimuComConfig config = new SimuComConfig(configuration.getAttributes(), true);
		final SimulationWorkflowConfiguration simulationWorkflowConfiguration = new SimulationWorkflowConfiguration(config);
		
		final WorkflowLaunchConfigurationBuilderInitialized builderEvent = new WorkflowLaunchConfigurationBuilderInitialized(configuration, simulationWorkflowConfiguration);
		systemDriver.postEvent(builderEvent);
		
		WorkflowConfigurationModule.simuComConfigProvider.set(config);
		return simulationWorkflowConfiguration;
	}

	@Override
	protected ArrayList<LoggerAppenderStruct> setupLogging(final Level logLevel) throws CoreException {
		final ArrayList<LoggerAppenderStruct> loggerList = super.setupLogging(Level.DEBUG); // Fixme
		loggerList.add(this.setupLogger("org.palladiosimulator.analyzer.slingshot", logLevel, logLevel == Level.DEBUG ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
		return loggerList;
	}
}
