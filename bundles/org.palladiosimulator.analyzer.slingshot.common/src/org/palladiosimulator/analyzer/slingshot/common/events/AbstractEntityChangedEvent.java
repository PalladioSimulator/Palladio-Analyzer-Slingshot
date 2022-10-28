package org.palladiosimulator.analyzer.slingshot.common.events;

import java.util.Objects;

public abstract class AbstractEntityChangedEvent<T> extends AbstractSimulationEvent {

	private final T entity;
	private final String id;
	
	public AbstractEntityChangedEvent(final T entity, final double delay) {
		super("", delay);
		this.entity = Objects.requireNonNull(entity);
		this.id = String.format("%s-%X", getClass().getSimpleName(), entity.hashCode());
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public T getEntity() {
		return entity;
	}
}
