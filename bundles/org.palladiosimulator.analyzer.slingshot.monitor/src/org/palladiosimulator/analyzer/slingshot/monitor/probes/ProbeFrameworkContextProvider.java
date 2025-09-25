package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;


@Singleton
public class ProbeFrameworkContextProvider implements Provider<ProbeFrameworkContext> {

	private final IGenericCalculatorFactory calculatorFactory;

	@Inject
	public ProbeFrameworkContextProvider(final IGenericCalculatorFactory calculatorFactory) {
		this.calculatorFactory = calculatorFactory;
	}

	@Override
	public ProbeFrameworkContext get() {
		return new ProbeFrameworkContext(this.calculatorFactory);
	}

}
