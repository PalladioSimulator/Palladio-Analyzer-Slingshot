package org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.EventContract;

public interface InterceptorInformation {
	
	public String getName();
	
	public Class<?> getHandlerType();
	
	public Optional<Class<?>> getEnclosingType();
	
	public Class<?> getEventType();
	
	public List<EventContract> getAssociatedContracts();
	
	
}
