package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.feedthrough;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class FeedThroughProcessingTypeModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(FeedThroughRecorderBehavior.class);
	}

}
