package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.aggregator;

import java.util.Optional;

import javax.measure.Measure;
import javax.measure.quantity.Duration;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.ProcessingTypeListener;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.MeasurementUpdated;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.MeasurementUpdated.MeasurementUpdateInformation;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementDrivenAggregation;
import org.palladiosimulator.monitorrepository.statisticalcharacterization.StatisticalCharacterizationAggregator;

public abstract class AbstractMeasurementAggregator extends ProcessingTypeListener {

	private final StatisticalCharacterizationAggregator aggregator;
	private final int frequencyOfAggregation;

	private int measurementUntilNextAggregation = 0;

	protected AbstractMeasurementAggregator(
			final MeasurementDrivenAggregation processingType,
			final MeasuringPoint measuringPoint,
			final NumericalBaseMetricDescription metricDescription) {
		super(processingType, measuringPoint, metricDescription);
		this.aggregator = processingType.getStatisticalCharacterization().getAggregator(metricDescription);
		this.frequencyOfAggregation = processingType.getFrequency();
		this.resetCounter();
	}

	public NumericalBaseMetricDescription getExpectedMetric() {
		return (NumericalBaseMetricDescription) this.getMetricDescription();
	}

	protected final void resetCounter() {
		this.measurementUntilNextAggregation = this.frequencyOfAggregation;
	}

	protected final void decrementCounter() {
		assert this.measurementUntilNextAggregation > 0;
		--this.measurementUntilNextAggregation;
	}

	@Override
	public final Result<MeasurementUpdated> onMeasurementMade(final MeasurementMade measurementMade) {
		if (!measurementMade.getEntity().isCompatibleWith(this.getMetricDescription())
				&& !MetricDescriptionUtility.isBaseMetricDescriptionSubsumedByMetricDescription(
						(BaseMetricDescription) this.getMetricDescription(),
						measurementMade.getEntity().getMetricDesciption())) {
			return Result.empty();
		}

		this.collectMeasurement(measurementMade.getEntity());
		this.decrementCounter();
		MeasurementUpdated updatedEvent = null;
		if (this.measurementUntilNextAggregation == 0) {
			if (this.aggregationRequired()) {
				this.onPreAggregate();
				updatedEvent = this.aggregate();
				this.onPostAggregate();
			}
			this.resetCounter();
		}
		if (updatedEvent != null) {
			return Result.of(updatedEvent);
		} else {
			return Result.empty();
		}
	}

	@Override
	public void preUnregister() {
		this.clear();
	}

	/**
	 * Discards all measurements collected for aggregation.
	 */
	protected abstract void clear();

	protected abstract boolean aggregationRequired();

	protected abstract Amount<Duration> getIntervalStartTime();

	protected abstract Amount<Duration> getIntervalEndTime();

	protected abstract Iterable<MeasuringValue> getDataToAggregate();

	protected abstract void collectMeasurement(final MeasuringValue newMeasurement);

	protected void onPreAggregate() {
	}

	protected void onPostAggregate() {
	}

	private MeasurementUpdated aggregate() {
		final MeasuringValue aggregatedData = this.aggregator.aggregateData(this.getDataToAggregate(),
				this.getIntervalStartTime(), this.getIntervalEndTime(), Optional.empty());
		final MeasurementUpdateInformation updateInformation = new MeasurementUpdateInformation(aggregatedData,
				this.getProcessingType(), this.getMeasuringPoint());
		return new MeasurementUpdated(updateInformation);
	}

	protected static Amount<Duration> getPointInTimeOfMeasurement(final MeasuringValue measurement) {
		assert measurement != null;

		final Measure<Double, Duration> pointInTimeMeasure = measurement
				.getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
		return Amount.valueOf(pointInTimeMeasure.getValue(), pointInTimeMeasure.getUnit());
	}
}
