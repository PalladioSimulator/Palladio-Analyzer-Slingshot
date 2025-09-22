package org.palladiosimulator.analyzer.slingshot.monitor.data.entities;

import java.util.List;
import java.util.Objects;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.measureprovider.IMeasureProvider;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.metricentity.IMetricEntity;

/**
 * This measuring value also carries the {@link MeasuringPoint} suggesting where the measurement had been made.
 * It simply delegates most of the methods to an existing {@link MeasuringValue}.
 * 
 * @author Julijan Katic
 */
public class SlingshotMeasuringValue extends MeasuringValue implements IMetricEntity, IMeasureProvider {
	
	private final MeasuringValue delegate;
	private final MeasuringPoint measuringPoint;
	
	/**
	 * Constructs a new measuring value containing a measuring point.
	 * 
	 * @param value The delegator (or original measuring value).
	 * @param point The measuring point.
	 */
	public SlingshotMeasuringValue(final MeasuringValue value, final MeasuringPoint point) {
		super(Objects.requireNonNull(value).getMetricDesciption()); // null not possible
		this.delegate = value;
		this.measuringPoint = Objects.requireNonNull(point);
	}

	@Override
	public <VALUE_TYPE, QUANTITY extends Quantity> Measure<VALUE_TYPE, QUANTITY> getMeasureForMetric(
			MetricDescription wantedMetric) {
		return this.delegate.getMeasureForMetric(wantedMetric);
	}

	@Override
	public List<Measure<?, ?>> asList() {
		return this.delegate.asList();
	}
	
	@Override
	public MetricDescription getMetricDesciption() {
		return this.delegate.getMetricDesciption();
	}

	@Override
	public boolean isCompatibleWith(MetricDescription other) {
		return this.delegate.isCompatibleWith(other);
	}

	@Override
	public Measure<?, ?>[] asArray() {
		return this.delegate.asArray();
	}

	@Override
	public MeasuringValue getMeasuringValueForMetric(MetricDescription metricDesciption) {
		return this.delegate.getMeasuringValueForMetric(metricDesciption);
	}

	public MeasuringPoint getMeasuringPoint() {
		return this.measuringPoint;
	}

}
