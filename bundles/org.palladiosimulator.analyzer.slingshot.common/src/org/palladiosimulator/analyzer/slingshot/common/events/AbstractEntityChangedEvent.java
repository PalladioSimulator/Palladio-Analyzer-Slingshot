package org.palladiosimulator.analyzer.slingshot.common.events;

import java.util.Objects;

public abstract class AbstractEntityChangedEvent<T> extends AbstractSimulationEvent {

	private final T entity;

	public AbstractEntityChangedEvent(final T entity, final double delay) {
		super(delay);
		this.entity = Objects.requireNonNull(entity);
	}
	
	public T getEntity() {
		return entity;
	}
}
