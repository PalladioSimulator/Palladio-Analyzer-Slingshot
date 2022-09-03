package org.palladiosimulator.analyzer.slingshot.core.driver;

import java.util.List;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.common.events.SystemEvent;
import org.palladiosimulator.analyzer.slingshot.core.annotations.BehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.core.extension.BehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;

import com.google.inject.Injector;

public class SlingshotSystemDriver implements SystemDriver {
	
	private final Bus systemBus;
	private final Injector parentInjector;
	private final List<BehaviorContainer> behaviorContainers;
	
	private boolean running = false;
	
	@Inject
	public SlingshotSystemDriver(
			final Injector parentInjector,
			@BehaviorExtensions final List<BehaviorContainer> behaviorContainer) {
		this.systemBus = Bus.instance("System");
		this.parentInjector = parentInjector;
		this.behaviorContainers = behaviorContainer;
		this.init();
	}
	
	private void init() {
		final Injector childInjector = this.parentInjector.createChildInjector(behaviorContainers);
		System.out.println("Initialize System extensions");
		System.out.println("Numbers of containers " + this.behaviorContainers.size());
		
		behaviorContainers.stream()
			.flatMap(extensions -> extensions.getExtensions().stream())
			.peek(simExtension -> System.out.println("Check " + simExtension.getSimpleName()))
			.forEach(simExtension -> {
				final Object e = childInjector.getInstance(simExtension);
				if (!(e instanceof SystemBehaviorExtension)) {
					return;
				}
				systemBus.register(e);
				System.out.println("Registered " + e.getClass().getSimpleName());
			});
	}

	@Override
	public void postEvent(SystemEvent systemEvent) {
		this.systemBus.post(systemEvent);
	}
	
	@Override
	public void postEventAndThen(final SystemEvent systemEvent, final Runnable runnable) {
		this.systemBus.post(systemEvent);
		runnable.run();
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}
	
}
