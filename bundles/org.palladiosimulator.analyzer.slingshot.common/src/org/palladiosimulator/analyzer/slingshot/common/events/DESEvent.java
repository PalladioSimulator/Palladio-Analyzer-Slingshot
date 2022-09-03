package org.palladiosimulator.analyzer.slingshot.common.events;

/**
 * This is an event that should happen during the simulation.
 * 
 * @author Julijan Katic
 *
 */
public interface DESEvent extends SlingshotEvent {
	
	public double time();
	
	public void setTime(final double time);
	
	public double delay();
	
}
