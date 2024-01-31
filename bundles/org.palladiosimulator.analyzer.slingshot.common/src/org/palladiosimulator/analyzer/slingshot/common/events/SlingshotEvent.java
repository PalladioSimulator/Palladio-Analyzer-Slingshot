package org.palladiosimulator.analyzer.slingshot.common.events;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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
	
	/**
	 * This field returns an unmodifiable map of meta information
	 * about the event, which other extensions might deem useful.
	 * 
	 * The reason for using a generic map to store meta information
	 * is to not break the interface contract, yet still provide
	 * possibly new information about the map.
	 * 
	 * The default behavior is returning an empty map.
	 * 
	 * @return A map of meta information.
	 */
	default public Optional<Object> getMetaInformation(final String key) {
		return Optional.empty();
	}
	
	default public void setMetaInformation(final String key, final Object value) { 
		// Do nothing
	}
	
}
