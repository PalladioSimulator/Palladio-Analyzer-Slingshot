package org.palladiosimulator.analyzer.slingshot.monitor.data.events;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.ProcessingTypeListener;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.monitorrepository.ProcessingType;

public final class ProcessingTypeRevealed extends AbstractSimulationEvent {

	private final ProcessingType processingType;
	private final MetricDescription metricDescription;
	private final MeasuringPoint measuringPoint;
	private final ProcessingTypeListener measurementSourceListener;
	private final boolean isSelfAdaptable;

	public ProcessingTypeRevealed(final ProcessingType processingType, final MeasuringPoint measuringPoint,
			final MetricDescription metricDescription,
			final ProcessingTypeListener measurementSourceListener,
			final boolean isSelfAdaptable) {
		this.processingType = processingType;
		this.metricDescription = metricDescription;
		this.measuringPoint = measuringPoint;
		this.measurementSourceListener = measurementSourceListener;
		this.isSelfAdaptable = isSelfAdaptable;
	}

	public ProcessingTypeRevealed(final ProcessingType processingType,
			final ProcessingTypeListener processingTypeListener) {
		this(processingType,
				processingType.getMeasurementSpecification().getMonitor().getMeasuringPoint(),
				processingType.getMeasurementSpecification().getMetricDescription(),
				processingTypeListener,
				processingType.getMeasurementSpecification().isTriggersSelfAdaptations());
	}

	/**
	 * @return the processingType
	 */
	public ProcessingType getProcessingType() {
		return this.processingType;
	}

	/**
	 * @return the metricDescription
	 */
	public MetricDescription getMetricDescription() {
		return this.metricDescription;
	}

	/**
	 * @return the measuringPoint
	 */
	public MeasuringPoint getMeasuringPoint() {
		return this.measuringPoint;
	}

	/**
	 * @return the measurementSourceListener
	 */
	public ProcessingTypeListener getMeasurementSourceListener() {
		return this.measurementSourceListener;
	}

	public boolean isSelfAdaptable() {
		return isSelfAdaptable;
	}

	@Override
	public String getName() {
		return "Processing Type Revealed";
	}

}
