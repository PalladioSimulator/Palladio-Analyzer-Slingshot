package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;
import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.InitializationStatus;

public final class ExtensionSimulationSnapshotInitialized extends AbstractSimulationEvent {

	private final String extensionId;
	private final InitializationStatus status;
	private final Optional<String> message;

	public ExtensionSimulationSnapshotInitialized(final String extensionId, final InitializationStatus status,
			final Optional<String> message) {
		super();
		this.extensionId = Objects.requireNonNull(extensionId);
		this.status = Objects.requireNonNull(status);
		this.message = Objects.requireNonNull(message);
	}

	public static ExtensionSimulationSnapshotInitialized success(final String extensionId) {
		return new ExtensionSimulationSnapshotInitialized(extensionId, InitializationStatus.SUCCESS, Optional.empty());
	}

	public static ExtensionSimulationSnapshotInitialized skipped(final String extensionId) {
		return new ExtensionSimulationSnapshotInitialized(extensionId, InitializationStatus.SKIPPED, Optional.empty());
	}

	public static ExtensionSimulationSnapshotInitialized failed(final String extensionId, final String message) {
		return new ExtensionSimulationSnapshotInitialized(extensionId, InitializationStatus.FAILED, Optional.of(message));
	}

	public String getExtensionId() {
		return this.extensionId;
	}

	public InitializationStatus getStatus() {
		return this.status;
	}

	public Optional<String> getMessage() {
		return this.message;
	}

}
