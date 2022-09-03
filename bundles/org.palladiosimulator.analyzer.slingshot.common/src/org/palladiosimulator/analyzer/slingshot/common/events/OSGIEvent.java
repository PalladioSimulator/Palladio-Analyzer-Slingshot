package org.palladiosimulator.analyzer.slingshot.common.events;

/**
 * An OSGI Event carries additional information from which bundle/plugin
 * the event has originated.
 * 
 * This is especially important if the event has an event-contract.
 *  
 * @author Julijan Katic
 *
 */
public interface OSGIEvent extends SlingshotEvent {
	
	public boolean fromBundleId(final String bundleId);
	
}
