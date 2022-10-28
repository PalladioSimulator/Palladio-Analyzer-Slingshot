package org.palladiosimulator.analyzer.slingshot.core.api;

import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

public interface SimulationDriver extends SimulationScheduling {
	
	public void init(final SimuComConfig config, final IProgressMonitor monitor);
	
	public void start();
	
	public void stop();
	
	public boolean isRunning();
	
}
