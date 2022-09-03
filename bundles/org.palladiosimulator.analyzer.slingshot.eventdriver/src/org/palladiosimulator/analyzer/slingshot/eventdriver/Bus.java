package org.palladiosimulator.analyzer.slingshot.eventdriver;

import org.palladiosimulator.analyzer.slingshot.eventdriver.internal.BusImplementation;

public interface Bus {

	public String getIdentifier();
	
	public void register(final Object object);
	
	public void unregister(final Object object);
	
	public void post(final Object event);
	
	public static Bus instance() {
		return new BusImplementation();
	}
	
	public static Bus instance(final String name) {
		return new BusImplementation(name);
	}

	public void closeRegistration();
}
