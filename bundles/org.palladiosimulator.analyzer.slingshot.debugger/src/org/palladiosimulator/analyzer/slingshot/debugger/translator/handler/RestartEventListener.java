package org.palladiosimulator.analyzer.slingshot.debugger.translator.handler;

import java.util.Optional;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.StartEventFromHereListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.StartSystemFromHereEvent;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;

public class RestartEventListener implements StartEventFromHereListener {

	private final Bus eventBus;

	public RestartEventListener(final Bus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void onEvent(final StartSystemFromHereEvent listenerEvent) {
		/*
		 * TODO: Research about restoring entire states, since the handlers themselves
		 * could hold state.
		 */

		System.out.println("Restart event from here: " + listenerEvent.getEventId());
		final Optional<DESEvent> debugEvent = EventDebugSystem.getEventHolder()
				.getCachedEvent(listenerEvent.getEventId()).map(ev -> (DESEvent) ev.getEvent());
		if (debugEvent.isPresent()) {
			System.out.println("Post this event again: " + debugEvent.get().getId());
			eventBus.post(debugEvent);
		}
	}

}
