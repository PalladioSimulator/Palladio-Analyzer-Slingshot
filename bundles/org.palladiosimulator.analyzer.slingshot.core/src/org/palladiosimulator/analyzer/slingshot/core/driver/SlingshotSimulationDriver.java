package org.palladiosimulator.analyzer.slingshot.core.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Singleton;

import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.core.annotations.SimulationBehaviorExtensions;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.behavior.CoreBehavior;
import org.palladiosimulator.analyzer.slingshot.core.behavior.CoreSnapshotBehavior;
import org.palladiosimulator.analyzer.slingshot.core.events.PreSimulationConfigurationStarted;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.core.events.snapshot.SimulationStateRestoreRequested;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.SnapshotCapableExtension;
import org.palladiosimulator.analyzer.slingshot.core.snapshot.DefaultSimulationStateValidator;
import org.palladiosimulator.analyzer.slingshot.core.snapshot.SimulationStateValidator;
import org.palladiosimulator.analyzer.slingshot.core.snapshot.SnapshotCaptureCoordinator;
import org.palladiosimulator.analyzer.slingshot.core.snapshot.SnapshotContributorRegistry;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;


import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;

import de.uka.ipd.sdq.simucomframework.core.SimuComConfig;

@Singleton
public class SlingshotSimulationDriver implements SimulationDriver {

	private boolean running = false;
	private boolean initialized = false;

	private final SimulationEngine engine;
	private final Injector parentInjector;

	private final List<SimulationBehaviorContainer> behaviorContainers;

	private IProgressMonitor monitor;
	private SimuComConfig config;
	private Optional<CompositeSimulationSnapshot> restoredState = Optional.empty();
	private SnapshotContributorRegistry contributorRegistry;
	private SimulationStateValidator stateValidator;

	@Inject
	public SlingshotSimulationDriver(final SimulationEngine engine, final Injector injector,
			@SimulationBehaviorExtensions final List<SimulationBehaviorContainer> behaviorContainers) {
		this.engine = engine;
		this.parentInjector = injector;
		this.behaviorContainers = behaviorContainers;
	}

	@Override
	public void init(final SimuComConfig config, final IProgressMonitor monitor) {
		this.init(config, monitor, Optional.empty());
	}

	@Override
	public void init(final SimuComConfig config, final IProgressMonitor monitor,
			final Optional<CompositeSimulationSnapshot> restoredState) {
		this.contributorRegistry = new SnapshotContributorRegistry();
		final SnapshotCaptureCoordinator captureCoordinator = new SnapshotCaptureCoordinator();
		this.stateValidator = new DefaultSimulationStateValidator();

		final List<Module> partitionIncludedStream = new ArrayList<>(behaviorContainers.size() + 1);
		partitionIncludedStream.add(new SimulationDriverSubModule(monitor, this.contributorRegistry, captureCoordinator,
				this.stateValidator));
		partitionIncludedStream.addAll(behaviorContainers);

		final Injector childInjector = this.parentInjector.createChildInjector(partitionIncludedStream);

		this.monitor = monitor;
		this.config = config;
		this.restoredState = Objects.requireNonNull(restoredState);

		behaviorContainers.stream().flatMap(behaviorContainer -> behaviorContainer.getExtensions().stream())
				.forEach(simExtensionClass -> {
					final Object extension = childInjector.getInstance(simExtensionClass);
					if (!(extension instanceof SimulationBehaviorExtension)) {
						return;
					}
					final SimulationBehaviorExtension simExtension = (SimulationBehaviorExtension) extension;
					if (simExtension.isActive()) {
						if (simExtension instanceof SnapshotCapableExtension snapshotCapable) {
							this.contributorRegistry.register(snapshotCapable);
						}
						engine.registerEventListener(simExtension);
					}
				});

		engine.registerEventListener(childInjector.getInstance(CoreSnapshotBehavior.class));
		engine.registerEventListener(new CoreBehavior(this));
		this.initialized = true;
	}

	@Override
	public void start() {
		if (this.isRunning() || !this.initialized) {
			return;
		}

		this.running = true;

		this.engine.init();
		this.restoredState.ifPresent(snapshot -> {
			this.stateValidator.validate(snapshot, this.contributorRegistry);
			this.scheduleEvent(new SimulationStateRestoreRequested(snapshot));
		});
		this.scheduleEvent(new PreSimulationConfigurationStarted());
		this.scheduleEvent(new SimulationStarted());
		this.scheduleEventAt(new SimulationFinished(), config.getSimuTime());
		this.engine.start();
	}

	@Override
	public void stop() {
		if (!this.isRunning()) {
			return;
		}

		this.running = false;
		this.engine.stop();
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public void scheduleEvent(final DESEvent event) {
		if (!this.isRunning()) {
			return;
		}

		this.engine.scheduleEvent(event);
	}

	@Override
	public void scheduleEventAt(final DESEvent event, final double simulationTime) {
		if (!this.isRunning()) {
			return;
		}

		this.engine.scheduleEventAt(event, simulationTime);
	}

	/**
	 * Module to provide Simulation Run Specific Instances, that already exist, such
	 * as the simuCom config and the progress monitor.
	 *
	 */
	private class SimulationDriverSubModule extends AbstractModule {

		private final IProgressMonitor monitor;
		private final SnapshotContributorRegistry contributorRegistry;
		private final SnapshotCaptureCoordinator captureCoordinator;
		private final SimulationStateValidator stateValidator;

		public SimulationDriverSubModule(final IProgressMonitor monitor,
				final SnapshotContributorRegistry contributorRegistry,
				final SnapshotCaptureCoordinator captureCoordinator,
				final SimulationStateValidator stateValidator) {

			this.monitor = monitor;
			this.contributorRegistry = contributorRegistry;
			this.captureCoordinator = captureCoordinator;
			this.stateValidator = stateValidator;
		}

		@Provides
		public IProgressMonitor monitor() {
			return this.monitor;
		}

		@Provides
		public SnapshotContributorRegistry contributorRegistry() {
			return this.contributorRegistry;
		}

		@Provides
		public SnapshotCaptureCoordinator captureCoordinator() {
			return this.captureCoordinator;
		}

		@Provides
		public SimulationStateValidator stateValidator() {
			return this.stateValidator;
		}

	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	@Override
	public <T extends DESEvent> void registerEventHandler(final Subscriber<T> subscriber) {
		this.engine.registerEventListener(subscriber);
	}

}
