package org.palladiosimulator.analyzer.slingshot.monitor.observer;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.core.api.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.SlingshotMeasuringValue;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.MeasurementMade;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;

/**
 * This class will "convert" the measurement source listener to an event-based
 * notifier. It listens whether a new measurement is available
 * ({@link MeasuringValue}) and publish the {@link MeasurementMade} event.
 * <p>
 * This approach is needed since the probe framework is using an observer
 * pattern.
 * 
 * @author Julijan Katic
 */
public class EventBasedMeasurementObserver implements IMeasurementSourceListener {

	private final SimulationScheduling simulationScheduling;
	
	@Inject
	public EventBasedMeasurementObserver(final SimulationScheduling simulationScheduling) {
		this.simulationScheduling = simulationScheduling;
	}
	
	@Override
	public void newMeasurementAvailable(MeasuringValue measuringValue) {
		if (!(measuringValue instanceof SlingshotMeasuringValue)) {
			throw new IllegalArgumentException("The measuring value must carry a measuring point (thus must be of type SlingshotMeasuringValue)");
		}
		this.simulationScheduling.scheduleEvent(new MeasurementMade((SlingshotMeasuringValue) measuringValue));
	}

	@Override
	public void preUnregister() {
		// do nothing
	}

}
