package org.palladiosimulator.analyzer.slingshot.core.events;

import java.util.Objects;
import java.util.UUID;

import com.google.common.base.Preconditions;

public abstract class AbstractEvent implements DESEvent {
	
	private final String id;
	private final double delay;
	
	private double scheduledTime;
	
	public AbstractEvent() {
		this(0);
	}
	
	public AbstractEvent(final String id, final double delay) {
		this.id = Objects.requireNonNull(id);
		this.delay = delay;
		
		Preconditions.checkArgument(delay >= 0, "Delay must be at least 0!");
	}
	
	public AbstractEvent(final double delay) {
		this(UUID.randomUUID().toString(), delay);
	}

	@Override
	public final String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public double time() {
		return this.scheduledTime;
	}
	
	@Override
	public final void setTime(final double time) {
		Preconditions.checkArgument(time >= 0, "Time must be at least 0!");
		this.scheduledTime = time;
	}
}
