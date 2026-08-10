package org.palladiosimulator.analyzer.slingshot.monitor;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.IObservableCalculatorRegistry;


@Singleton
public class ObservableCalculatorRegistryProvider implements Provider<IObservableCalculatorRegistry> {

	private final ProbeFrameworkContext probeFrameworkContext;

	@Inject
	public ObservableCalculatorRegistryProvider(final ProbeFrameworkContext probeFrameworkContext) {
		this.probeFrameworkContext = probeFrameworkContext;
	}

	@Override
	public IObservableCalculatorRegistry get() {
		return probeFrameworkContext.getCalculatorRegistry();
	}

}
