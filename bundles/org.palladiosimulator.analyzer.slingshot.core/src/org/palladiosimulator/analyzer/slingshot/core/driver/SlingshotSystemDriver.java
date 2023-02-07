package org.palladiosimulator.analyzer.slingshot.core.driver;

import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.palladiosimulator.analyzer.slingshot.common.events.SystemEvent;
import org.palladiosimulator.analyzer.slingshot.core.annotations.SystemBehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.api.SystemDriver;
import org.palladiosimulator.analyzer.slingshot.core.behavior.CoreBehavior;
import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

import com.google.inject.Injector;

@Singleton
public class SlingshotSystemDriver implements SystemDriver {
	
	private final Bus systemBus;
	private final Injector parentInjector;
	private final List<SystemBehaviorContainer> behaviorContainers;
	
	private boolean running = false;
	
	@Inject
	public SlingshotSystemDriver(
			final Injector parentInjector,
			@SystemBehaviorExtensions final List<SystemBehaviorContainer> behaviorContainer) {
		this.systemBus = Bus.instance("System");
		this.parentInjector = parentInjector;
		this.behaviorContainers = behaviorContainer;
		this.init();
	}
	
	private void init() {
		final Injector childInjector = this.parentInjector.createChildInjector(behaviorContainers);
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

	@Override
	public void registerHandlers(SimulationBehaviorExtension handlers) {
		this.systemBus.register(handlers);
	}

	@Override
	public <T> void registerHandler(String id, Class<T> forEvent, Function<T, Result<?>> handler) {
		this.systemBus.registerHandler(id, forEvent, handler);
	}
	
}
