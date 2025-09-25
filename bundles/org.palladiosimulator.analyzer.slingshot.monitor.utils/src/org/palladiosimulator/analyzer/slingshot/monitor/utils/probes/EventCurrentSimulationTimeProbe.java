package org.palladiosimulator.analyzer.slingshot.monitor.utils.probes;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedBasicProbe;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventDistinguisher;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 * A standard implementation of a {@link DESEvent}-based probe that takes the
 * current simulation time. The MetricDescription is
 * {@link MetricDescriptionConstants#POINT_IN_TIME_METRIC}.
 *
 * @author Julijan Katic
 */
public final class EventCurrentSimulationTimeProbe extends EventBasedBasicProbe<Double, Duration> {

	/**
	 * Constructs a EventCurrentSimulationTimeProbe.
	 */
	public EventCurrentSimulationTimeProbe() {
		super(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
	}

	/**
	 * Constructs an EventCurrentSimulationTimeProbe with a custom distinguisher.
	 *
	 * @param distinguisher The distinguisher that instantiates a
	 *                      {@link RequestContext}.
	 */
	public EventCurrentSimulationTimeProbe(
			final EventDistinguisher distinguisher) {
		super(MetricDescriptionConstants.POINT_IN_TIME_METRIC, distinguisher);
	}

	@Override
	public Measure<Double, Duration> getMeasurement(final DESEvent event) {
		return Measure.valueOf(event.time(), SI.SECOND);
	}


}
