package org.palladiosimulator.analyzer.slingshot.workflow.jobs;

import org.eclipse.debug.core.ILaunch;
import org.palladiosimulator.analyzer.slingshot.workflow.SimulationWorkflowConfiguration;
import org.palladiosimulator.analyzer.workflow.core.jobs.LoadModelIntoBlackboardJob;
import org.palladiosimulator.analyzer.workflow.core.jobs.PreparePCMBlackboardPartitionJob;

import de.uka.ipd.sdq.workflow.jobs.ICompositeJob;
import de.uka.ipd.sdq.workflow.jobs.SequentialBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimulationRootJob extends SequentialBlackboardInteractingJob<MDSDBlackboard> implements ICompositeJob {

	public SimulationRootJob(final SimulationWorkflowConfiguration config, final ILaunch launch) {
		super(SimulationRootJob.class.getName(), false);

		this.addJob(new PreparePCMBlackboardPartitionJob());
		
		config.getPCMModelFiles().forEach(modelFile -> LoadModelIntoBlackboardJob.parseUriAndAddModelLoadJob(modelFile, this));
		this.addJob(new SimulationJob(config.getSimuComConfig()));
	}

}
