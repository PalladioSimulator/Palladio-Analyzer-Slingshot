package org.palladiosimulator.analyzer.slingshot.workflow;

import javax.inject.Provider;
import javax.inject.Singleton;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

/**
 * A provider for the {@link SimuComConfig} object that holds
 * all the information about the simulation.
 *
 */
@Singleton
public class SimuComConfigProvider implements Provider<SimuComConfig> {

	private SimuComConfig config;

	public void set(final SimuComConfig config) {
		this.config = config;
	}

	@Override
	public SimuComConfig get() {
		return config;
	}

}
