package org.palladiosimulator.analyzer.slingshot.debugger.translator;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.analyzer.slingshot.debugger.translator.handler.RestartEventListener;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;

public final class DebuggingEnabledEventBusFactory {

	private DebuggingEnabledEventBusFactory() {
	}

	public static Bus createBus(final String name) {
		final Bus bus = Bus.instance(name);
		bus.addEventListener(new EventConverter());
		bus.register(new EventHandlerInterceptor());

		final RestartEventListener rel = new RestartEventListener(bus);
		EventDebugSystem.addStartFromHereListener(rel);

		return bus;
	}

}
