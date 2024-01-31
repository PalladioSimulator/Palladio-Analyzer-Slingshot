package org.palladiosimulator.analyzer.slingshot.core.driver;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.analyzer.slingshot.common.events.SystemEvent;
import org.palladiosimulator.analyzer.slingshot.core.annotations.SystemBehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.debugger.translator.DebuggingEnabledEventBusFactory;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;

import com.google.inject.Injector;

@Singleton
public class SlingshotSystemDriver implements SystemDriver {

	private final Bus systemBus;
	private final Injector parentInjector;
	private final List<SystemBehaviorContainer> behaviorContainers;

	private final boolean running = false;

	@Inject
	public SlingshotSystemDriver(
			final Injector parentInjector,
			@SystemBehaviorExtensions final List<SystemBehaviorContainer> behaviorContainer) {

		final String eventBusName = "Slingshot-System";
		if (EventDebugSystem.isDebugEnabled()) {
			systemBus = DebuggingEnabledEventBusFactory.createBus(eventBusName);
		} else {
			systemBus = Bus.instance(eventBusName);
		}
		
		this.parentInjector = parentInjector;
		behaviorContainers = behaviorContainer;
		this.init();
	}

	private void init() {
		final Injector childInjector = parentInjector.createChildInjector(behaviorContainers);
		//System.out.println("Initialize System extensions");
		//System.out.println("Numbers of containers " + this.behaviorContainers.size());

		behaviorContainers.stream()
			.flatMap(extensions -> extensions.getExtensions().stream())
		//	.peek(simExtension -> System.out.println("Check " + simExtension.getSimpleName()))
			.forEach(simExtension -> {
				final Object e = childInjector.getInstance(simExtension);
				if (!(e instanceof SystemBehaviorExtension)) {
					return; // Ignore extensions that don't explicitly declare that they have SystemBehaviorExtensions
				}
				systemBus.register(e);
		//		System.out.println("Registered " + e.getClass().getSimpleName());
			});
	}

	@Override
	public void postEvent(final SystemEvent systemEvent) {
		systemBus.post(systemEvent);
	}

	@Override
	public void postEventAndThen(final SystemEvent systemEvent, final Runnable runnable) {
		systemBus.post(systemEvent);
		runnable.run();
	}

	@Override
	public boolean isRunning() {
		return running;
	}

}
