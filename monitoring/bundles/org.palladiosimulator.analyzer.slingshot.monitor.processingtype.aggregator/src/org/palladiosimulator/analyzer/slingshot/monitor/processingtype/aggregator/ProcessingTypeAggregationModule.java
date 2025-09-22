package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.aggregator;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class ProcessingTypeAggregationModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(MeasurementsAggregatorBehavior.class);
	}

	
	
}
