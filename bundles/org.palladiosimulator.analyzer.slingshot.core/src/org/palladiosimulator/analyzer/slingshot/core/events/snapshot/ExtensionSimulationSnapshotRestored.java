package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;
import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.RestoreStatus;

public final class ExtensionSimulationSnapshotRestored extends AbstractSimulationEvent {

	private final String extensionId;
	private final RestoreStatus status;
	private final Optional<String> message;

	public ExtensionSimulationSnapshotRestored(final String extensionId, final RestoreStatus status,
			final Optional<String> message) {
		super();
		this.extensionId = Objects.requireNonNull(extensionId);
		this.status = Objects.requireNonNull(status);
		this.message = Objects.requireNonNull(message);
	}

	public static ExtensionSimulationSnapshotRestored success(final String extensionId) {
		return new ExtensionSimulationSnapshotRestored(extensionId, RestoreStatus.SUCCESS, Optional.empty());
	}

	public static ExtensionSimulationSnapshotRestored skipped(final String extensionId) {
		return new ExtensionSimulationSnapshotRestored(extensionId, RestoreStatus.SKIPPED, Optional.empty());
	}

	public static ExtensionSimulationSnapshotRestored failed(final String extensionId, final String message) {
		return new ExtensionSimulationSnapshotRestored(extensionId, RestoreStatus.FAILED, Optional.of(message));
	}

	public String getExtensionId() {
		return this.extensionId;
	}

	public RestoreStatus getStatus() {
		return this.status;
	}

	public Optional<String> getMessage() {
		return this.message;
	}

}
