package org.palladiosimulator.analyzer.slingshot.core.exceptions;

/**
 * Thrown when a snapshot cannot be applied to the current simulation run.
 */
public final class SimulationStateIncompatibleException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SimulationStateIncompatibleException(final String message) {
		super(message);
	}

}
