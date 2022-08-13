package org.palladiosimulator.analyzer.slingshot.core.events;

public interface DESEvent extends SlingshotEvent {
	
	public double time();
	
	public void setTime(final double time);
	
	public double delay();
	
}
