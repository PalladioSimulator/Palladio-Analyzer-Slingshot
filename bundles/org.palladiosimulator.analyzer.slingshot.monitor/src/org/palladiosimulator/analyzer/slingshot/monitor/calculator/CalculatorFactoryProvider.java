package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.palladiosimulator.analyzer.slingshot.monitor.observer.EventBasedMeasurementObserver;
import org.palladiosimulator.analyzer.slingshot.monitor.recorder.decorator.MonitoringAttachingCalculatorFactoryDecorator;
import org.palladiosimulator.analyzer.slingshot.monitor.recorder.decorator.RecorderAttachingCalculatorFactoryDecorator;
import org.palladiosimulator.probeframework.calculator.ExtensibleCalculatorFactoryDelegatingFactory;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.calculator.RegisterCalculatorFactoryDecorator;

@Singleton
public class CalculatorFactoryProvider implements Provider<IGenericCalculatorFactory> {

	private final SimuComConfig simuComConfig;
	private final EventBasedMeasurementObserver observer;

	private final IGenericCalculatorFactory factory;

	@Inject
	public CalculatorFactoryProvider(final SimuComConfig simuComConfig, final EventBasedMeasurementObserver observer) {
		this.simuComConfig = simuComConfig;
		this.observer = observer;

		// setup factory here, because registry must be same for all extension.
		final IGenericCalculatorFactory parentFactory = new ExtensibleCalculatorFactoryDelegatingFactory();
		final IGenericCalculatorFactory recorderAttachedFactory = new RecorderAttachingCalculatorFactoryDecorator(parentFactory, simuComConfig.getRecorderName(), simuComConfig.getRecorderConfigurationFactory());
		final IGenericCalculatorFactory monitorAttachedFactory = new MonitoringAttachingCalculatorFactoryDecorator(recorderAttachedFactory, this.observer);
		final IGenericCalculatorFactory registeringFactory = new RegisterCalculatorFactoryDecorator(
				monitorAttachedFactory);

		this.factory = registeringFactory;
	}

	@Override
	public IGenericCalculatorFactory get() {
		return factory;
	}
}
