package org.palladiosimulator.analyzer.slingshot.common.events.modelchanges;


import java.util.List;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;

public final class ModelAdjusted extends AbstractSimulationEvent {
	
	private final boolean wasSuccessful;
	private final List<ModelChange<?>> changes;
	
	public ModelAdjusted(boolean wasSuccessful, List<ModelChange<?>> changes) {
		super();
		this.wasSuccessful = wasSuccessful;
		this.changes = changes;
	}

	public boolean isWasSuccessful() {
		return wasSuccessful;
	}

	public List<ModelChange<?>> getChanges() {
		return changes;
	}
	
	
}
