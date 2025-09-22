package org.palladiosimulator.analyzer.slingshot.monitor.data.events;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;

public interface MonitoringEvent extends DESEvent {

	@Override
	default double delay() {
		return 0;
	}
	
}
