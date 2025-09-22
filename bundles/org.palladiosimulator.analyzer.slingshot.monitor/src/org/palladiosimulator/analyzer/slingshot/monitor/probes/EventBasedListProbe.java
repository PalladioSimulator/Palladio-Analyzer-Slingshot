package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import java.util.List;
import java.util.function.Function;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.SI;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.measurementframework.BasicMeasurement;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.measureprovider.MeasurementListMeasureProvider;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.probeframework.measurement.ProbeMeasurement;

/**
 * A probe that is {@link DESEvent}-based and probes for a
 * {@link MeasurementListMeasureProvider}.
 *
 * Measurements/Probes are taken when the associated {@link DESEvent} was
 * published.
 *
 * Beware: This probe requires a {@link MetricSetDescription}.
 *
 * Beware: MetricSetDescriptions are define as (time, value)-pair or as (value, time)-pair. This Probe makes sure to keep thing in the correct order.
 *
 * @author Sarah Stieß
 *
 * @param <V> The value type of the measurement.
 * @param <Q> The quantity type of the measurement.
 */
public abstract class EventBasedListProbe<V, Q extends Quantity> extends EventBasedProbe<V, Q> {

	private final Function<DESEvent, MeasuringValue> firstvalue;
	private final Function<DESEvent, MeasuringValue> secondvalue;

	/**
	 * Constructs an event-based probe with
	 * {@link EventDistinguisher#DEFAULT_DISTINGUISHER}.
	 *
	 * @param metricDescription A metric description needed by the super-class.
	 */
	protected EventBasedListProbe(final MetricDescription metricDescription) {
		this(metricDescription, EventDistinguisher.DEFAULT_DISTINGUISHER);
	}

	/**
	 * Constructs an event-based probe.
	 *
	 * @param metricDescription A metric description needed by the super-class.
	 * @param distinguisher     The distinguisher that is used for creating
	 *                          {@link RequestContext}s.
	 */
	public EventBasedListProbe(final MetricDescription metricDescription, final EventDistinguisher distinguisher) {
		super(metricDescription, distinguisher);

		if (!(metricDescription instanceof MetricSetDescription)) {
			throw new IllegalArgumentException(String.format(
					"Illegal Metric Decription for Unary Calculator. Got %s %s but require a MetricSetDescription.",
					metricDescription.getClass().getSimpleName(), metricDescription.getName()));
		}

		final List<MetricDescription> descriptions = ((MetricSetDescription) this.getMetricDesciption())
				.getSubsumedMetrics();

		if (descriptions.get(0).equals(MetricDescriptionConstants.POINT_IN_TIME_METRIC)) {
			firstvalue = this::getTime;
			secondvalue = (final DESEvent e) -> this.getValue(e, (BaseMetricDescription) descriptions.get(1));

		} else if (descriptions.get(1).equals(MetricDescriptionConstants.POINT_IN_TIME_METRIC)) {
			secondvalue = this::getTime;
			firstvalue = (final DESEvent e) -> this.getValue(e, (BaseMetricDescription) descriptions.get(0));

		} else {
			throw new IllegalArgumentException(
					String.format("Expected MetricSetDescription with at least one point in time metric, but got %s.",
							metricDescription.getName()));
		}
	}

	/**
	 * Extract measuring value for the point in time of the probe measurement.
	 *
	 * @param event Event to extract time from.
	 * @return time for the probe measurement.
	 */
	private MeasuringValue getTime(final DESEvent event) {
		return new BasicMeasurement<>(Measure.valueOf(event.time(), SI.SECOND),
				MetricDescriptionConstants.POINT_IN_TIME_METRIC)
						.getMeasuringValueForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
	}

	/**
	 *
	 * Extract measuring value for the value of the probe measurement
	 *
	 * @param event Event to extract the value from.
	 * @param desc  Metric description of the value to be extracted.
	 * @return value for the probe measurement.
	 */
	private MeasuringValue getValue(final DESEvent event, final BaseMetricDescription desc) {
		return new BasicMeasurement<>(this.getMeasurement(event), desc).getMeasuringValueForMetric(desc);
	}

	@Override
	protected ProbeMeasurement getProbeMeasurement(final DESEvent event) {
		final MeasurementListMeasureProvider resultMeasurement = new MeasurementListMeasureProvider(
				List.of(firstvalue.apply(event), secondvalue.apply(event)));
		return new ProbeMeasurement(resultMeasurement, this, this.getDistinguisher().apply(event));
	}
}
