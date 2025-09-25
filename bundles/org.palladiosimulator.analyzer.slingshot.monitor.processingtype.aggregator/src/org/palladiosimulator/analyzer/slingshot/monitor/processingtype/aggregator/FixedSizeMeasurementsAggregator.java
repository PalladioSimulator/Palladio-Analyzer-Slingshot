package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.aggregator;

import java.util.Arrays;
import java.util.Iterator;

import javax.measure.quantity.Duration;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.monitorrepository.FixedSizeAggregation;

public final class FixedSizeMeasurementsAggregator extends AbstractMeasurementAggregator {

	private final InternalBuffer internalBuffer;

	public FixedSizeMeasurementsAggregator(final FixedSizeAggregation processingType,
			final MeasuringPoint measuringPoint, final NumericalBaseMetricDescription metricDescription) {
		super(processingType, measuringPoint, metricDescription);
		// TODO Auto-generated constructor stub

		this.internalBuffer = new InternalBuffer(processingType.getNumberOfMeasurements());
	}

	public FixedSizeMeasurementsAggregator(final FixedSizeAggregation fixedSizeAggregation) {
		this(fixedSizeAggregation,
				fixedSizeAggregation.getMeasurementSpecification().getMonitor().getMeasuringPoint(),
				(NumericalBaseMetricDescription) fixedSizeAggregation.getMeasurementSpecification()
						.getMetricDescription());
	}

	@Override
	protected void clear() {
		this.internalBuffer.clear();
	}

	@Override
	protected boolean aggregationRequired() {
		return this.internalBuffer.isFull();
	}

	@Override
	protected Amount<Duration> getIntervalStartTime() {
		return getPointInTimeOfMeasurement(this.internalBuffer.getEldestElement());
	}

	@Override
	protected Amount<Duration> getIntervalEndTime() {
		return getPointInTimeOfMeasurement(this.internalBuffer.getNewestElement());
	}

	@Override
	protected Iterable<MeasuringValue> getDataToAggregate() {
		return this.internalBuffer;
	}

	@Override
	protected void collectMeasurement(final MeasuringValue newMeasurement) {
		this.internalBuffer.add(newMeasurement);
	}

	private static final class InternalBuffer implements Iterable<MeasuringValue> {
		private final MeasuringValue[] data;
		private int eldestElementPointer = 0;
		private int currentElementPointer = 0;

		private InternalBuffer(final int capacity) {
			assert capacity > 0;
			this.data = new MeasuringValue[capacity];
		}

		public int size() {
			return this.currentElementPointer;
		}

		public int capacity() {
			return this.data.length;
		}

		public boolean isFull() {
			return this.size() == this.capacity();
		}

		public void add(final MeasuringValue measuringValue) {
			if (this.isFull()) {
				this.addFull(measuringValue);
			} else {
				this.addNotFull(measuringValue);
			}
		}

		private void addFull(final MeasuringValue measuringValue) {
			this.data[this.eldestElementPointer] = measuringValue;
			this.eldestElementPointer++;
			this.eldestElementPointer %= this.capacity();
		}

		private void addNotFull(final MeasuringValue measuringValue) {
			this.data[this.currentElementPointer++] = measuringValue;
		}

		public void clear() {
			Arrays.fill(this.data, null);
			this.eldestElementPointer = this.currentElementPointer = 0;
		}

		public MeasuringValue getNewestElement() {
			return this.data[(this.eldestElementPointer + this.size() - 1) % this.capacity()];
		}

		public MeasuringValue getEldestElement() {
			return this.data[this.eldestElementPointer];
		}

		@Override
		public Iterator<MeasuringValue> iterator() {
			return Arrays.asList(this.data).iterator();
		}
	}
}
