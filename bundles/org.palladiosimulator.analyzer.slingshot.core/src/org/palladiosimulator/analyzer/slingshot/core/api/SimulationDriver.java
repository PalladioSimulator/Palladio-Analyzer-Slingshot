package org.palladiosimulator.analyzer.slingshot.core.api;

import java.util.function.Function;

import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

public interface SimulationDriver extends SimulationScheduling, BusBasedDriver {
	
	public void init(final SimuComConfig config, final IProgressMonitor monitor);
	
	public void start();
	
	public void stop();
	
	public boolean isRunning();
	
}
