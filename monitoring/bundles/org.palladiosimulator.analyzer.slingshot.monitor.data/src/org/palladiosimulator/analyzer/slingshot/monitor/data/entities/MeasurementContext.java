package org.palladiosimulator.analyzer.slingshot.monitor.data.entities;

import java.util.Objects;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.ProcessingType;

public final class MeasurementContext {

	private final MeasuringValue measuringValue;
	private final ProcessingType processingType;
	private final MeasuringPoint measuringPoint;
	private final Monitor monitor;

	private MeasurementContext(final Builder builder) {
		this.measuringPoint = builder.measuringPoint;
		this.measuringValue = builder.measuringValue;
		this.processingType = builder.processingType;
		this.monitor = builder.monitor;
	}

	/**
	 * @return the measuringValue
	 */
	public MeasuringValue getMeasuringValue() {
		return this.measuringValue;
	}

	/**
	 * @return the processingType
	 */
	public ProcessingType getProcessingType() {
		return this.processingType;
	}

	/**
	 * @return the measuringPoint
	 */
	public MeasuringPoint getMeasuringPoint() {
		return this.measuringPoint;
	}

	/**
	 * @return the monitor
	 */
	public Monitor getMonitor() {
		return this.monitor;
	}

	public static final class Builder {
		private MeasuringValue measuringValue;
		private ProcessingType processingType;
		private MeasuringPoint measuringPoint;
		private Monitor monitor;

		private Builder() {
		}

		public Builder withMeasuringValue(final MeasuringValue value) {
			this.measuringValue = Objects.requireNonNull(value);
			return this;
		}

		public Builder withProcessingType(final ProcessingType type) {
			this.processingType = Objects.requireNonNull(type);
			return this;
		}

		public Builder withMeasuringPoint(final MeasuringPoint point) {
			this.measuringPoint = Objects.requireNonNull(point);
			return this;
		}

		public Builder withMonitor(final Monitor monitor) {
			this.monitor = Objects.requireNonNull(monitor);
			return this;
		}

		public MeasurementContext build() {
			return new MeasurementContext(this);
		}
	}
}
