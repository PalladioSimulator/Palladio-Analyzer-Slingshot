package org.palladiosimulator.analyzer.slingshot.core.events.snapshot;

import java.util.Objects;
import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.InitializationStatus;

/**
 * DES event emitted by a snapshot-capable extension after processing
 * {@link SimulationStateInitializationRequested}.
 *
 * <p>
 * Reports whether the extension successfully applied its snapshot, was skipped
 * (no matching contributor in the composite), or failed.
 * </p>
 */
public final class ExtensionSimulationSnapshotInitialized extends AbstractSimulationEvent {

	private final String extensionId;
	private final InitializationStatus status;
	private final Optional<String> message;

	/**
	 * Creates an initialization result event.
	 *
	 * @param extensionId  the contributing extension identifier
	 * @param status       the initialization outcome
	 * @param message      optional failure or diagnostic message
	 */
	public ExtensionSimulationSnapshotInitialized(final String extensionId, final InitializationStatus status,
			final Optional<String> message) {
		super();
		this.extensionId = Objects.requireNonNull(extensionId);
		this.status = Objects.requireNonNull(status);
		this.message = Objects.requireNonNull(message);
	}

	/**
	 * Creates a successful initialization result.
	 *
	 * @param extensionId the contributing extension identifier
	 * @return the result event
	 */
	public static ExtensionSimulationSnapshotInitialized success(final String extensionId) {
		return new ExtensionSimulationSnapshotInitialized(extensionId, InitializationStatus.SUCCESS, Optional.empty());
	}

	/**
	 * Creates a skipped initialization result (no matching snapshot in the composite).
	 *
	 * @param extensionId the contributing extension identifier
	 * @return the result event
	 */
	public static ExtensionSimulationSnapshotInitialized skipped(final String extensionId) {
		return new ExtensionSimulationSnapshotInitialized(extensionId, InitializationStatus.SKIPPED, Optional.empty());
	}

	/**
	 * Creates a failed initialization result.
	 *
	 * @param extensionId the contributing extension identifier
	 * @param message     a description of the failure
	 * @return the result event
	 */
	public static ExtensionSimulationSnapshotInitialized failed(final String extensionId, final String message) {
		return new ExtensionSimulationSnapshotInitialized(extensionId, InitializationStatus.FAILED, Optional.of(message));
	}

	/**
	 * Returns the extension identifier that produced this result.
	 *
	 * @return the extension identifier
	 */
	public String getExtensionId() {
		return this.extensionId;
	}

	/**
	 * Returns the initialization outcome.
	 *
	 * @return the status
	 */
	public InitializationStatus getStatus() {
		return this.status;
	}

	/**
	 * Returns an optional failure or diagnostic message.
	 *
	 * @return the message, or empty if none
	 */
	public Optional<String> getMessage() {
		return this.message;
	}

}
