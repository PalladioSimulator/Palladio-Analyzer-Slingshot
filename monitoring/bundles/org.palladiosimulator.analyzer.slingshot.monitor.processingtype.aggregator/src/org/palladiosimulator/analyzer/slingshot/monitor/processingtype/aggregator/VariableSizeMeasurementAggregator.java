package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.aggregator;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

import javax.measure.Measure;
import javax.measure.quantity.Duration;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.monitorrepository.MonitorRepositoryPackage;
import org.palladiosimulator.monitorrepository.VariableSizeAggregation;

public final class VariableSizeMeasurementAggregator extends AbstractMeasurementAggregator {

	private final Deque<MeasuringValue> buffer;
	private final VariableSizeAggregation variableSizeAggregation;
	private final Amount<Duration> retrospectionLength;

	private static final Amount<Duration> ZERO_DURATION = Amount.valueOf(0, Duration.UNIT);

	public VariableSizeMeasurementAggregator(
			final VariableSizeAggregation processingType,
			final MeasuringPoint measuringPoint,
			final NumericalBaseMetricDescription metricDescription) {
		super(processingType, measuringPoint, metricDescription);
		this.buffer = new LinkedList<>();
		this.variableSizeAggregation = processingType;
		final Measure<Double, Duration> retrospectionMeasure = this.variableSizeAggregation
				.getRetrospectionLengthAsMeasure();

		if (retrospectionMeasure.compareTo(ZERO_DURATION) <= 0) {
			throw new IllegalStateException("Value of '"
					+ MonitorRepositoryPackage.Literals.VARIABLE_SIZE_AGGREGATION__RETROSPECTION_LENGTH.getName()
					+ "' attribute of '" + this.variableSizeAggregation.eClass().getName() + "' with id "
					+ this.variableSizeAggregation.getId() + " must be positive!");
		}

		this.retrospectionLength = Amount.valueOf(retrospectionMeasure.getValue(), retrospectionMeasure.getUnit());
	}

	public VariableSizeMeasurementAggregator(final VariableSizeAggregation variableSizeAggregation) {
		this(variableSizeAggregation,
				variableSizeAggregation.getMeasurementSpecification().getMonitor().getMeasuringPoint(),
				(NumericalBaseMetricDescription) variableSizeAggregation.getMeasurementSpecification()
						.getMetricDescription());
	}

	@Override
	protected void clear() {
		this.buffer.clear();
	}

	@Override
	protected boolean aggregationRequired() {
		return !this.buffer.isEmpty() && !getPointInTimeOfMeasurement(this.buffer.getLast())
				.minus(this.retrospectionLength)
				.isLessThan(getPointInTimeOfMeasurement(this.buffer.getFirst()));
	}

	@Override
	protected Amount<Duration> getIntervalStartTime() {
		final Amount<Duration> result = this.getIntervalEndTime().minus(this.retrospectionLength);
		return result.compareTo(ZERO_DURATION) < 0 ? ZERO_DURATION : result;
	}

	@Override
	protected Amount<Duration> getIntervalEndTime() {
		return getPointInTimeOfMeasurement(this.buffer.getLast());
	}

	@Override
	protected Iterable<MeasuringValue> getDataToAggregate() {
		return Collections.unmodifiableCollection(this.buffer);
	}

	@Override
	protected void collectMeasurement(final MeasuringValue newMeasurement) {
		this.buffer.add(newMeasurement);
	}

	@Override
	protected void onPreAggregate() {
		this.evictMeasurements();
	}

	private void evictMeasurements() {
		switch (this.getExpectedMetric().getScopeOfValidity()) {
		case CONTINUOUS:
			MeasuringValue first = this.buffer.peekFirst();
			MeasuringValue lastPolled = null;
			while (first != null && getPointInTimeOfMeasurement(this.buffer.getLast())
					.minus(getPointInTimeOfMeasurement(first))
					.isGreaterThan(this.retrospectionLength)) {
				lastPolled = this.buffer.pollFirst();
				first = this.buffer.peekFirst();
			}
			if (lastPolled != null) {
				this.buffer.addFirst(lastPolled);
			}
			break;
		case DISCRETE:
			while (!this.buffer.isEmpty() && getPointInTimeOfMeasurement(this.buffer.getLast())
					.minus(getPointInTimeOfMeasurement(this.buffer.getFirst()))
					.isGreaterThan(this.retrospectionLength)) {
				this.buffer.pollFirst();
			}
			break;
		}
	}
}
