package org.palladiosimulator.analyzer.slingshot.core.api;

public interface SimulationDriver extends SimulationScheduling {
	
	public void start();
	
	public void stop();
	
	public boolean isRunning();
	
}
