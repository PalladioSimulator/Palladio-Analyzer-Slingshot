package org.palladiosimulator.analyzer.slingshot.common.events;

import org.eclipse.jdt.annotation.NonNull;

public abstract class AbstractSystemEvent extends AbstractEvent implements SystemEvent {

	public AbstractSystemEvent(final String id) {
		super(id);
	}
	
	public AbstractSystemEvent() {
		super();
	}
	
	@Override
	public @NonNull String getName() {
		return getClass().getCanonicalName();
	}
}
