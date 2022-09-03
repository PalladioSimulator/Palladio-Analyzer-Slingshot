package org.palladiosimulator.analyzer.slingshot.common.events;

import java.util.Objects;
import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

import com.google.common.base.Preconditions;

/**
 * 
 * 
 * @author Julijan Katic
 *
 */
public abstract class AbstractSimulationEvent extends AbstractEvent implements DESEvent, OSGIEvent {
	
	@Nullable private final String bundleId;
	private double scheduledTime;
	private final double delay;
	
	public AbstractSimulationEvent() {
		this(0);
	}
	
	public AbstractSimulationEvent(final String id, final double delay, @Nullable final String bundleId) {
		super(id);
		this.delay = delay;
		this.bundleId = bundleId;
		
		Preconditions.checkArgument(delay >= 0, "Delay must be at least 0!");
	}
	
	public AbstractSimulationEvent(final double delay) {
		this(delay, null);
	}
	
	public AbstractSimulationEvent(final double delay, @Nullable final String bundleId) {
		this(UUID.randomUUID().toString(), delay, null);
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
	
	@Override
	public double delay() {
		return this.delay;
	}
	
	@Override
	public final boolean fromBundleId(final String bundleId) {
		return this.bundleId == null || this.bundleId.equalsIgnoreCase(bundleId);
	}
}
