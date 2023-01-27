package org.palladiosimulator.analyzer.slingshot.core.api;

import java.util.function.Consumer;

import org.palladiosimulator.analyzer.slingshot.common.events.SystemEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public interface SystemDriver extends BusBasedDriver {

	void postEvent(final SystemEvent systemEvent);
	
	void postEventAndThen(final SystemEvent systemEvent, final Runnable after);
	
	boolean isRunning();
	
}
