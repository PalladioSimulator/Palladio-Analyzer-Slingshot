package org.palladiosimulator.analyzer.slingshot.monitor.interpreter;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.common.annotations.Nullable;
import org.palladiosimulator.analyzer.slingshot.common.events.modelchanges.ModelAdjusted;
import org.palladiosimulator.analyzer.slingshot.common.events.modelchanges.MonitorChange;
import org.palladiosimulator.analyzer.slingshot.core.events.PreSimulationConfigurationStarted;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.MonitoringEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MonitorModelVisited;
import org.palladiosimulator.monitorrepository.MonitorRepository;

@OnEvent(when = PreSimulationConfigurationStarted.class, then = { MonitorModelVisited.class }, cardinality = EventCardinality.MANY)
@OnEvent(when = ModelAdjusted.class, then = { MonitorModelVisited.class }, cardinality = EventCardinality.MANY)
public class MonitorRepositoryInterpreterBehavior implements SimulationBehaviorExtension {

	private final MonitorRepository monitorRepository;

	@Inject
	public MonitorRepositoryInterpreterBehavior(@Nullable final MonitorRepository monitorRepository) {
		this.monitorRepository = monitorRepository;
	}

	@Override
	public boolean isActive() {
		return this.monitorRepository != null && !this.monitorRepository.getMonitors().isEmpty();
	}
	
	@Subscribe
	public Result<MonitoringEvent> onPreSimulationConfigurationStarted(final PreSimulationConfigurationStarted event) {
		return Result.of(this.interpreteMonitorModel(monitorRepository));
	}

	@Subscribe
	public Result<MonitoringEvent> onModelAdjusted(final ModelAdjusted modelAdjusted) {
		final Set<MonitoringEvent> result =	modelAdjusted.getChanges().stream()
				.filter(MonitorChange.class::isInstance)
				.map(MonitorChange.class::cast)
				.flatMap(monitorChange -> this.interpreteMonitorModel(monitorChange.getNewMonitor()).stream())
				.collect(Collectors.toSet());

		return Result.of(result);
	}

	private Set<MonitoringEvent> interpreteMonitorModel(final EObject modelElement) {
		final MonitorModelVisitor visitor = new MonitorModelVisitor();
		return visitor.doSwitch(modelElement);
	}

}
