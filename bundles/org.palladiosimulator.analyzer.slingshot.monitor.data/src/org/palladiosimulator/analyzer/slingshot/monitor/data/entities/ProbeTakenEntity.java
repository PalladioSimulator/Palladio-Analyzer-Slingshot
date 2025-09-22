package org.palladiosimulator.analyzer.slingshot.monitor.data.entities;

import javax.annotation.processing.Generated;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.probeframework.probes.Probe;

public final class ProbeTakenEntity {

	private final Probe probe;
	private final MeasuringPoint measuringPoint;

	@Generated("SparkTools")
	private ProbeTakenEntity(final Builder builder) {
		this.probe = builder.probe;
		this.measuringPoint = builder.measuringPoint;
	}

	/**
	 * @return the probe
	 */
	public Probe getProbe() {
		return this.probe;
	}

	/**
	 * @return the measuringPoint
	 */
	public MeasuringPoint getMeasuringPoint() {
		return this.measuringPoint;
	}

	/**
	 * Creates builder to build {@link ProbeTakenEntity}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link ProbeTakenEntity}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private Probe probe;
		private MeasuringPoint measuringPoint;

		private Builder() {
		}

		/**
		 * Builder method for probe parameter.
		 * 
		 * @param probe field to set
		 * @return builder
		 */
		public Builder withProbe(final Probe probe) {
			this.probe = probe;
			return this;
		}

		/**
		 * Builder method for measuringPoint parameter.
		 * 
		 * @param measuringPoint field to set
		 * @return builder
		 */
		public Builder withMeasuringPoint(final MeasuringPoint measuringPoint) {
			this.measuringPoint = measuringPoint;
			return this;
		}

		/**
		 * Builder method of the builder.
		 * 
		 * @return built class
		 */
		public ProbeTakenEntity build() {
			return new ProbeTakenEntity(this);
		}
	}

}
