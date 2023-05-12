package org.palladiosimulator.analyzer.slingshot.core.extension;

public interface SimulationBehaviorExtension {

	/**
	 * Tells whether this extension should be activated by the
	 * simulation driver. This is important especially if the extension
	 * should NOT be activated if the underlying model used to simulate
	 * is optional itself and hence not provided.
	 * 
	 * The default implementation always returns {@code true}.
	 * 
	 * @return true iff this extension should be activated and used by the
	 * 		   driver.
	 */
	public default boolean isActive() {
		return true;
	}
	
}
