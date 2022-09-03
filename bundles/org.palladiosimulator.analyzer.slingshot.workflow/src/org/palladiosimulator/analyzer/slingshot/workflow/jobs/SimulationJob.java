package org.palladiosimulator.analyzer.slingshot.workflow.jobs;

import org.eclipse.core.runtime.IProgressMonitor;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimulationJob implements IBlackboardInteractingJob<MDSDBlackboard> {

	private MDSDBlackboard blackboard;
	
	
	@Override
	public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		
	}

	@Override
	public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
		
	}

	@Override
	public String getName() {
		return SimulationJob.class.getCanonicalName();
	}

	@Override
	public void setBlackboard(MDSDBlackboard blackboard) {
		this.blackboard = blackboard;
	}

}
