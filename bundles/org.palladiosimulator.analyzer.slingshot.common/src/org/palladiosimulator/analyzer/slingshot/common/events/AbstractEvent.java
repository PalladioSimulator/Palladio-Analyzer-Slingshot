package org.palladiosimulator.analyzer.slingshot.common.events;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractEvent implements SlingshotEvent {

	private final String id;
	
	public AbstractEvent(final String id) {
		this.id = Objects.requireNonNull(id);
	}
	
	public AbstractEvent() {
		this(UUID.randomUUID().toString());
	}
	
	@Override
	public String getId() {
		return this.id;
	}

}
