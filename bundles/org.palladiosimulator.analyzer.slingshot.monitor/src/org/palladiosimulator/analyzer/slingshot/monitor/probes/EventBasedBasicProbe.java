package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import javax.measure.quantity.Quantity;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.measurementframework.BasicMeasurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.measurement.ProbeMeasurement;

/**
 * A probe that is {@link DESEvent}-based and probes for a
 * {@link BasicMeasurement}.
 *
 * Measurements/Probes are taken when the associated {@link DESEvent} was
 * published.
 *
 * @author Julijan Katic
 *
 * @param <V> The value type of the measurement.
 * @param <Q> The quantity type of the measurement.
 */
public abstract class EventBasedBasicProbe<V, Q extends Quantity> extends EventBasedProbe<V, Q> {

	/**
	 * Constructs an event-based probe with
	 * {@link EventDistinguisher#DEFAULT_DISTINGUISHER}.
	 *
	 * @param metricDescription A metric description needed by the super-class.
	 */
	protected EventBasedBasicProbe(final MetricDescription metricDescription) {
		super(metricDescription);
	}

	/**
	 * Constructs an event-based probe.
	 *
	 * @param metricDescription A metric description needed by the super-class.
	 * @param distinguisher     The distinguisher that is used for creating
	 *                          {@link RequestContext}s.
	 */
	public EventBasedBasicProbe(final MetricDescription metricDescription,
			final EventDistinguisher distinguisher) {
		super(metricDescription, distinguisher);
	}

	@Override
	protected ProbeMeasurement getProbeMeasurement(final DESEvent event) {
		final BasicMeasurement<V, Q> resultMeasurement = new BasicMeasurement<>(
				this.getMeasurement(event), (BaseMetricDescription) this.getMetricDesciption());
		return new ProbeMeasurement(resultMeasurement, this, this.getDistinguisher().apply(event));
	}
}
