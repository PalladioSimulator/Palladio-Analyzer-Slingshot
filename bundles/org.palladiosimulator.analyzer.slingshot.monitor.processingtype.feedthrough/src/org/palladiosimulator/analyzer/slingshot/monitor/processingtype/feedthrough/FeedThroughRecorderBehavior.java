package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.feedthrough;

import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.ProcessingTypeRevealed;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MonitorModelVisited;
import org.palladiosimulator.monitorrepository.FeedThrough;

@OnEvent(when = MonitorModelVisited.class, then = ProcessingTypeRevealed.class, cardinality = EventCardinality.SINGLE)
public class FeedThroughRecorderBehavior implements SimulationBehaviorExtension {

	@Subscribe(reified = FeedThrough.class)
	public Result<ProcessingTypeRevealed> onFeedThroughProcessingTypeVisited(final MonitorModelVisited<FeedThrough> feedThroughEvent) {
		return Result.of(new ProcessingTypeRevealed(feedThroughEvent.getEntity(),
				new FeedThroughRecorder(feedThroughEvent.getEntity())));
	}

}
