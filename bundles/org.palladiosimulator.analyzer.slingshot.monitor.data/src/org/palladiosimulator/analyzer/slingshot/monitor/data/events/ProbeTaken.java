package org.palladiosimulator.analyzer.slingshot.monitor.data.events;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.ProbeTakenEntity;

public final class ProbeTaken extends AbstractEntityChangedEvent<ProbeTakenEntity> {

	public ProbeTaken(final ProbeTakenEntity entity) {
		super(entity, 0);
	}

}
