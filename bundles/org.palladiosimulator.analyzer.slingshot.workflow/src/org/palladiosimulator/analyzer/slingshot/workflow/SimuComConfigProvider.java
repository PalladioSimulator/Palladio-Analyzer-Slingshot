package org.palladiosimulator.analyzer.slingshot.workflow;

import javax.inject.Provider;
import javax.inject.Singleton;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

@Singleton
class SimuComConfigProvider implements Provider<SimuComConfig> {

	private SimuComConfig config;
	
	void set(final SimuComConfig config) {
		this.config = config;
	}
	
	@Override
	public SimuComConfig get() {
		return config;
	}

}
