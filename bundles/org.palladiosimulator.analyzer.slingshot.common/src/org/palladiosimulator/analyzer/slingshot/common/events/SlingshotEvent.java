package org.palladiosimulator.analyzer.slingshot.common.events;

/**
 * This interface is the upper-most event type for any event that
 * should be considered by Slingshot.
 * 
 * Each event must have a unique ID and a name. The ID is to
 * distinguish between different events; if they have the same
 * id, even if they are different objects/instanciations, they
 * should be treated as the same.
 * 
 * @author Julijan Katic
 *
 */
public interface SlingshotEvent {
	
	/**
	 * The unique identifiable String of this event.
	 * @return a non-null unique id.
	 */
	public String getId();
	
	/**
	 * The name of this event. Does not have to be unique.
	 * @return a non-null name.
	 */
	public String getName();
	
}
