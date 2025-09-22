package org.palladiosimulator.analyzer.slingshot.monitor.data.events;

import javax.annotation.processing.Generated;

import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.probeframework.calculator.Calculator;

/**
 * Entity describing what measurement by which calculator and monitor has been
 * taken.
 * 
 * @author Julijan Katic
 *
 */
public final class MeasurementEntity {

	private final MeasuringValue measuringValue;
	private final Calculator calculator;

	@Generated("SparkTools")
	private MeasurementEntity(final Builder builder) {
		this.measuringValue = builder.measuringValue;
		this.calculator = builder.calculator;
	}

	/**
	 * Creates builder to build {@link MeasurementEntity}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link MeasurementEntity}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private MeasuringValue measuringValue;
		private Calculator calculator;

		private Builder() {
		}

		/**
		 * Builder method for measuringValue parameter.
		 * 
		 * @param measuringValue field to set
		 * @return builder
		 */
		public Builder withMeasuringValue(final MeasuringValue measuringValue) {
			this.measuringValue = measuringValue;
			return this;
		}

		/**
		 * Builder method for calculator parameter.
		 * 
		 * @param calculator field to set
		 * @return builder
		 */
		public Builder withCalculator(final Calculator calculator) {
			this.calculator = calculator;
			return this;
		}

		/**
		 * Builder method of the builder.
		 * 
		 * @return built class
		 */
		public MeasurementEntity build() {
			return new MeasurementEntity(this);
		}
	}

}
