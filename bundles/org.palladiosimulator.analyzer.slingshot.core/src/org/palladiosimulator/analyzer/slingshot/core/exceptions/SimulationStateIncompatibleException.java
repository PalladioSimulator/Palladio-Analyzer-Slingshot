package org.palladiosimulator.analyzer.slingshot.core.exceptions;

/**
 * Thrown when a {@link org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot}
 * cannot be applied to the current simulation run.
 *
 * <p>
 * Typical causes include a contributor set mismatch, an unsupported {@code schemaVersion},
 * or an incompatible simulation context.
 * </p>
 */
public final class SimulationStateIncompatibleException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception with the given diagnostic message.
	 *
	 * @param message describes why the snapshot is incompatible
	 */
	public SimulationStateIncompatibleException(final String message) {
		super(message);
	}

}
