package org.palladiosimulator.analyzer.slingshot.monitor.utils;

import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.SlingshotMeasuringValue;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;
import org.palladiosimulator.probeframework.calculator.Calculator;

/**
 * Utility class for wrapping measurement source listeners (such as calculators) to instead
 * accept {@link SlingshotMeasuringValue} values for an event-based communication.
 * 
 * @author Julijan Katic
 *
 */
public final class SlingshotCalculatorWrapper {
	
	public static IMeasurementSourceListener wrap(final Calculator calculator, final IMeasurementSourceListener sourceListener) {
		return wrap(calculator.getMeasuringPoint(), sourceListener);
	}
	
	public static IMeasurementSourceListener wrap(final MeasuringPoint measuringPoint, final IMeasurementSourceListener delegate) {
		return new IMeasurementSourceListener() {

			@Override
			public void newMeasurementAvailable(MeasuringValue newMeasurement) {
				final SlingshotMeasuringValue measuringValue = new SlingshotMeasuringValue(newMeasurement, measuringPoint);
				delegate.newMeasurementAvailable(measuringValue);
			}

			@Override
			public void preUnregister() {
				delegate.preUnregister();
			}
			
		};
	}
	
}
