package org.palladiosimulator.analyzer.slingshot.common.events;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public abstract class AbstractEvent implements SlingshotEvent {

	private final String id;
	
	private final Map<String, Object> metaInformation = new HashMap<>();
	
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

	@Override
	public Optional<Object> getMetaInformation(final String key) {
		return Optional.ofNullable(metaInformation.get(key));
	}

	@Override
	public void setMetaInformation(String key, Object value) {
		metaInformation.put(key, value);
	}
	
}
