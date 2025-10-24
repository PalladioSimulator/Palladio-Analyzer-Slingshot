package org.palladiosimulator.analyzer.slingshot.monitor.recorder.decorator;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.CalculatorProbeSet;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.recorderframework.core.IRecorder;
import org.palladiosimulator.recorderframework.core.config.AbstractRecorderConfiguration;
import org.palladiosimulator.recorderframework.core.config.IRecorderConfiguration;
import org.palladiosimulator.recorderframework.core.config.IRecorderConfigurationFactory;
import org.palladiosimulator.recorderframework.core.utils.RecorderExtensionHelper;

public class RecorderAttachingCalculatorFactoryDecorator implements IGenericCalculatorFactory {

	private final IGenericCalculatorFactory delegator;
	private final String recorderName;
	private final IRecorderConfigurationFactory configurationFactory;

	public RecorderAttachingCalculatorFactoryDecorator(final IGenericCalculatorFactory delegator,
			final String recorderName,
			final IRecorderConfigurationFactory configurationFactory) {
		this.delegator = delegator;
		this.recorderName = recorderName;
		this.configurationFactory = configurationFactory;
	}

	
	@Override
	public Calculator buildCalculator(MetricDescription arg0, MeasuringPoint arg1, CalculatorProbeSet arg2) {
		return this.setupCalculator(this.delegator.buildCalculator(arg0, arg1, arg2));
	}

	/**
	 * Helper method to setup the new calculator.
	 * 
	 * @param calculator the calculator to setup.
	 * @return the set-up calculator.
	 */
	private Calculator setupCalculator(final Calculator calculator) {
		final Map<String, Object> recorderConfigurationMap = new HashMap<>(2);
		recorderConfigurationMap.put(AbstractRecorderConfiguration.RECORDER_ACCEPTED_METRIC,
				calculator.getMetricDesciption());
		recorderConfigurationMap.put(AbstractRecorderConfiguration.MEASURING_POINT, calculator.getMeasuringPoint());

		final IRecorder recorder = RecorderExtensionHelper
				.instantiateRecorderImplementationForRecorder(this.recorderName);
		final IRecorderConfiguration recorderConfiguration = this.configurationFactory
				.createRecorderConfiguration(recorderConfigurationMap);

		recorder.initialize(recorderConfiguration);
		calculator.addObserver(recorder);
		return calculator;
	}

}
