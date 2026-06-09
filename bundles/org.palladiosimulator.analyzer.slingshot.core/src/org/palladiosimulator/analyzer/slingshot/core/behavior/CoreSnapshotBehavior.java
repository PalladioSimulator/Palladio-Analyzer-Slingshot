package org.palladiosimulator.analyzer.slingshot.core.behavior;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CoreSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationInformation;
import org.palladiosimulator.analyzer.slingshot.core.events.snapshot.ExtensionSimulationSnapshotCaptured;
import org.palladiosimulator.analyzer.slingshot.core.events.snapshot.SimulationSnapshotCompleted;
import org.palladiosimulator.analyzer.slingshot.core.events.snapshot.SimulationSnapshotRequested;
import org.palladiosimulator.analyzer.slingshot.core.events.snapshot.SimulationStateInitializationRequested;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.core.snapshot.SnapshotCaptureCoordinator;
import org.palladiosimulator.analyzer.slingshot.core.snapshot.SnapshotContributorRegistry;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.PostIntercept;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

@OnEvent(when = SimulationSnapshotRequested.class, then = { SimulationSnapshotCompleted.class }, cardinality = EventCardinality.SINGLE)
@OnEvent(when = ExtensionSimulationSnapshotCaptured.class, then = { SimulationSnapshotCompleted.class }, cardinality = EventCardinality.SINGLE)
@OnEvent(when = SimulationStateInitializationRequested.class, then = {}, cardinality = EventCardinality.SINGLE)
public class CoreSnapshotBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = LogManager.getLogger(CoreSnapshotBehavior.class);

	private final SimulationDriver simulationDriver;
	private final SimulationInformation simulationInformation;
	private final SnapshotContributorRegistry contributorRegistry;
	private final SnapshotCaptureCoordinator captureCoordinator;

	@Inject
	public CoreSnapshotBehavior(final SimulationDriver simulationDriver, final SimulationInformation simulationInformation,
			final SnapshotContributorRegistry contributorRegistry, final SnapshotCaptureCoordinator captureCoordinator) {
		this.simulationDriver = simulationDriver;
		this.simulationInformation = simulationInformation;
		this.contributorRegistry = contributorRegistry;
		this.captureCoordinator = captureCoordinator;
	}

	@Subscribe(priority = 100)
	public void onSimulationSnapshotRequested(final SimulationSnapshotRequested request) {
		final CoreSimulationSnapshot coreSnapshot = new CoreSimulationSnapshot(this.simulationInformation.currentSimulationTime(),
				this.simulationInformation.consumedEvents());
		this.captureCoordinator.startCapture(request.getRequestId(), this.contributorRegistry.getActiveContributorIds(),
				coreSnapshot);
		LOGGER.debug("Started snapshot capture for request " + request.getRequestId());
	}

	@PostIntercept
	public InterceptionResult collectCapturedSnapshots(final InterceptorInformation interceptionInformation,
			final SimulationSnapshotRequested request, final Result<?> result) {
		result.getResultEvents().stream()
				.filter(ExtensionSimulationSnapshotCaptured.class::isInstance)
				.map(ExtensionSimulationSnapshotCaptured.class::cast)
				.forEach(this::processCapturedSnapshot);
		return InterceptionResult.success();
	}

	@Subscribe
	public Result<SimulationSnapshotCompleted> onExtensionSimulationSnapshotCaptured(
			final ExtensionSimulationSnapshotCaptured captured) {
		return this.processCapturedSnapshot(captured);
	}

	@Subscribe
	public void onSimulationStateInitializationRequested(final SimulationStateInitializationRequested request) {
		LOGGER.debug("Simulation state initialization requested with contributors: "
				+ request.getSnapshot().contributorIds());
	}

	private Result<SimulationSnapshotCompleted> processCapturedSnapshot(
			final ExtensionSimulationSnapshotCaptured captured) {
		if (!this.captureCoordinator.isCaptureActive()
				|| !this.captureCoordinator.getActiveRequestId().equals(captured.getRequestId())) {
			return Result.empty();
		}

		final Optional<CompositeSimulationSnapshot> composite = this.captureCoordinator
				.addContributorSnapshot(captured.getSnapshot());
		if (composite.isEmpty()) {
			return Result.empty();
		}

		final SimulationSnapshotCompleted completed = new SimulationSnapshotCompleted(captured.getRequestId(),
				composite.get(), true);
		this.simulationDriver.scheduleEvent(completed);
		return Result.of(completed);
	}

}
