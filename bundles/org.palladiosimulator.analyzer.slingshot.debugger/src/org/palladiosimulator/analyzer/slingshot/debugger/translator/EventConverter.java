package org.palladiosimulator.analyzer.slingshot.debugger.translator;

import java.util.function.Consumer;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.common.events.SystemEvent;

public class EventConverter implements Consumer<Object> {

	@Override
	public void accept(final Object t) {
		final IDebugEvent debugEvent;
		if (t instanceof final DESEvent desEvent) {
			debugEvent = new DebuggedDESEvent(desEvent);
		} else if (t instanceof final SystemEvent systemEvent) {
			debugEvent = new DebuggedSystemEvent(systemEvent);
		} else {
			return;
		}

//		EventCache.getInstance().getCache().put(debugEvent.getId(), debugEvent);
		EventDebugSystem.getEventHolder().addEvent(debugEvent);
		EventDebugSystem.provideEvent(debugEvent);
	}

}
