package org.palladiosimulator.analyzer.slingshot.core.api;

import org.eclipse.core.runtime.IProgressMonitor;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;


import de.uka.ipd.sdq.simucomframework.core.SimuComConfig;

public interface SimulationDriver extends SimulationScheduling {

	public void init(final SimuComConfig config, final IProgressMonitor monitor);

	public void start();

	public void stop();

	public boolean isRunning();
	
	public boolean isInitialized();
	
	public <T extends DESEvent> void registerEventHandler(final Subscriber<T> subscriber);

}
