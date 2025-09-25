package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.aggregator;

import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.ProcessingTypeRevealed;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MonitorModelVisited;
import org.palladiosimulator.monitorrepository.FixedSizeAggregation;
import org.palladiosimulator.monitorrepository.VariableSizeAggregation;


@OnEvent(when = MonitorModelVisited.class, then = ProcessingTypeRevealed.class, cardinality = EventCardinality.SINGLE)
public class MeasurementsAggregatorBehavior implements SimulationBehaviorExtension {

	@Subscribe(reified = FixedSizeAggregation.class)
	public Result<ProcessingTypeRevealed> onFixedSizeAggregationVisited(final MonitorModelVisited<FixedSizeAggregation> fixedSizeAggregationVisited) {
		return Result.of(new ProcessingTypeRevealed(fixedSizeAggregationVisited.getEntity(),
				new FixedSizeMeasurementsAggregator(fixedSizeAggregationVisited.getEntity())));
	}

	@Subscribe(reified = VariableSizeAggregation.class)
	public Result<ProcessingTypeRevealed> onVariableSizeAggregationVisited(final MonitorModelVisited<VariableSizeAggregation> variableSizeAggregationVisited) {
		return Result.of(new ProcessingTypeRevealed(variableSizeAggregationVisited.getEntity(),
				new VariableSizeMeasurementAggregator(variableSizeAggregationVisited.getEntity())));
	}

}
