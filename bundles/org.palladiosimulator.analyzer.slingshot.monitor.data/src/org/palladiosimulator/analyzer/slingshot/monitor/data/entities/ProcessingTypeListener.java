package org.palladiosimulator.analyzer.slingshot.monitor.data.entities;

import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.MeasurementUpdated;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.monitorrepository.ProcessingType;

public abstract class ProcessingTypeListener {

	private final ProcessingType processingType;
	private final MeasuringPoint measuringPoint;
	private final MetricDescription metricDescription;

	protected ProcessingTypeListener(
			final ProcessingType processingType,
			final MeasuringPoint measuringPoint,
			final MetricDescription metricDescription) {
		this.processingType = processingType;
		this.measuringPoint = measuringPoint;
		this.metricDescription = metricDescription;
	}

	public void preUnregister() {
		/* Do nothing in the standard implementation. */
	}

	public abstract Result<MeasurementUpdated> onMeasurementMade(final MeasurementMade measurementMade);

	/**
	 * @return the processingType
	 */
	protected final ProcessingType getProcessingType() {
		return this.processingType;
	}

	/**
	 * @return the measuringPoint
	 */
	protected final MeasuringPoint getMeasuringPoint() {
		return this.measuringPoint;
	}

	/**
	 * @return the metricDescription
	 */
	protected final MetricDescription getMetricDescription() {
		return this.metricDescription;
	}

}
