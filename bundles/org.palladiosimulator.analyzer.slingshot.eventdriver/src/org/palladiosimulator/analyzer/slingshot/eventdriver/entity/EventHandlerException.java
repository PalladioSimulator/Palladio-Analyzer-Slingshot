package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import java.util.Optional;

public class EventHandlerException extends Exception {

	private final Object event;
	private final String eventHandlerName;
	private final Optional<Class<?>> enclosingType;

	public EventHandlerException(final Object event, final Optional<Class<?>> enclosingType,
			final String eventHandlerName,
			final Throwable ex) {
		super(ex);
		this.event = event;
		this.eventHandlerName = eventHandlerName;
		this.enclosingType = enclosingType;
	}

	public Object getEvent() {
		return event;
	}

	public String getEventHandlerName() {
		return eventHandlerName;
	}

	public Optional<Class<?>> getEnclosingType() {
		return enclosingType;
	}

}
