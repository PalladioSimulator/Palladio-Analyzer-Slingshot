package org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.ProcessingType;

public final class MeasurementSpecificationVisited extends MonitorModelVisited<MeasurementSpecification> {
	
	private final MeasuringPoint measuringPoint;
	private final ProcessingType processingType;
	private final MetricDescription metricDescription;

	public MeasurementSpecificationVisited(final MeasurementSpecification entity) {
		super(entity);
		this.measuringPoint = entity.getMonitor().getMeasuringPoint();
		this.processingType = entity.getProcessingType();
		this.metricDescription = entity.getMetricDescription();
	}

	public MeasuringPoint getMeasuringPoint() {
		return measuringPoint;
	}

	public ProcessingType getProcessingType() {
		return processingType;
	}
	
	public MetricDescription getMetricDescription() {
		return this.metricDescription;
	}
	
}
