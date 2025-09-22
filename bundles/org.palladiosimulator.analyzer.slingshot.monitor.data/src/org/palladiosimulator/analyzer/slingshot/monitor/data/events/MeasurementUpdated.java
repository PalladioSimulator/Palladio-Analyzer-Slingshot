package org.palladiosimulator.analyzer.slingshot.monitor.data.events;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.MeasurementUpdated.MeasurementUpdateInformation;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.monitorrepository.ProcessingType;

public final class MeasurementUpdated extends AbstractEntityChangedEvent<MeasurementUpdateInformation> {

	public MeasurementUpdated(final MeasurementUpdateInformation entity) {
		super(entity, 0);
	}

	public static final class MeasurementUpdateInformation {
		private final MeasuringValue measuringValue;
		private final ProcessingType processingType;
		private final MeasuringPoint measuringPoint;

		public MeasurementUpdateInformation(final MeasuringValue measuringValue, final ProcessingType processingType,
				final MeasuringPoint measuringPoint) {
			this.measuringValue = measuringValue;
			this.processingType = processingType;
			this.measuringPoint = measuringPoint;
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

	}
}
