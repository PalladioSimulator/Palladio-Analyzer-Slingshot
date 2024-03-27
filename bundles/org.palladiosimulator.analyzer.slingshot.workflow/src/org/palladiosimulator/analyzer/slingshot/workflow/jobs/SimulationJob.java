package org.palladiosimulator.analyzer.slingshot.workflow.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.slingshot.workflow.WorkflowConfigurationModule;
import org.palladiosimulator.analyzer.workflow.ConstantsContainer;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimulationJob implements IBlackboardInteractingJob<MDSDBlackboard> {

	private static final Logger LOGGER = LogManager.getLogger(SimulationJob.class);

	private MDSDBlackboard blackboard;
	private final SimulationDriver simulationDriver;
	private final PCMResourceSetPartitionProvider pcmResourceSetPartition;
	private final SimuComConfig simuComConfig;

	public SimulationJob(final SimuComConfig simuComConfig) {
		this.simulationDriver = Slingshot.getInstance().getSimulationDriver();
		this.pcmResourceSetPartition = Slingshot.getInstance().getInstance(PCMResourceSetPartitionProvider.class);
		this.simuComConfig = simuComConfig;
	}

	@Override
	public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		final PCMResourceSetPartition partition = (PCMResourceSetPartition) this.blackboard
				.getPartition(ConstantsContainer.DEFAULT_PCM_INSTANCE_PARTITION_ID);

		WorkflowConfigurationModule.simuComConfigProvider.set(simuComConfig);
		WorkflowConfigurationModule.blackboardProvider.set(blackboard);
		this.pcmResourceSetPartition.set(partition);
		LOGGER.debug("Current partition: ");
		partition.getResourceSet().getResources().forEach(resource -> LOGGER.debug("Resource: " + resource.getURI().path()));

		LOGGER.debug("monitor: " + monitor.getClass().getName());
		monitor.beginTask("Start Simulation", 3);

		monitor.subTask("Initialize driver");
		simulationDriver.init(simuComConfig, monitor);
		monitor.worked(1);

		monitor.subTask("Start simulation");
		simulationDriver.start();
		monitor.worked(1);

		monitor.subTask("Restore");
		monitor.worked(1);

		monitor.done();
	}

	@Override
	public void cleanup(final IProgressMonitor monitor) throws CleanupFailedException {

	}

	@Override
	public String getName() {
		return SimulationJob.class.getCanonicalName();
	}

	@Override
	public void setBlackboard(final MDSDBlackboard blackboard) {
		this.blackboard = blackboard;
	}

}
