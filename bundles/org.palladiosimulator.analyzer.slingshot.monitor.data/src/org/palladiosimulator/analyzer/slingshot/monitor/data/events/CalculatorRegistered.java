package org.palladiosimulator.analyzer.slingshot.monitor.data.events;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;
import org.palladiosimulator.probeframework.calculator.Calculator;

public final class CalculatorRegistered extends AbstractEntityChangedEvent<Calculator> {

	public CalculatorRegistered(final Calculator entity) {
		super(entity, 0);
	}

}
