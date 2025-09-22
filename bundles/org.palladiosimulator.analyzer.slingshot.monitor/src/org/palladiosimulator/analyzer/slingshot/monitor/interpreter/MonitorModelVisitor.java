package org.palladiosimulator.analyzer.slingshot.monitor.interpreter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.MonitoringEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MeasurementSpecificationVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MonitorModelVisited;
import org.palladiosimulator.monitorrepository.ArithmeticMean;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.monitorrepository.ProcessingType;
import org.palladiosimulator.monitorrepository.StatisticalCharacterization;
import org.palladiosimulator.monitorrepository.util.MonitorRepositorySwitch;

public class MonitorModelVisitor extends MonitorRepositorySwitch<Set<MonitoringEvent>> {

	@Override
	public Set<MonitoringEvent> caseArithmeticMean(final ArithmeticMean object) {
		// TODO Auto-generated method stub
		return super.caseArithmeticMean(object);
	}

	@Override
	public Set<MonitoringEvent> caseMonitor(final Monitor object) {
		if (!object.isActivated()) {
			return Set.of();
		}

		final int numberMeasurementSpecs = object.getMeasurementSpecifications().size();

		final Set<MonitoringEvent> result = new HashSet<>(numberMeasurementSpecs);

		object.getMeasurementSpecifications().stream()
			  .flatMap(spec -> this.doSwitch(spec).stream())
			  .forEach(result::add);

		return result;
	}

	@Override
	public Set<MonitoringEvent> caseMonitorRepository(final MonitorRepository object) {
		return object.getMonitors().stream()
				.flatMap(monitor -> this.doSwitch(monitor).stream())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<MonitoringEvent> caseMeasurementSpecification(final MeasurementSpecification object) {
		final Set<MonitoringEvent> events = new HashSet<>(2);

		events.addAll(this.doSwitch(object.getProcessingType()));
		events.add(new MeasurementSpecificationVisited(object));

		return events;
	}

	@Override
	public Set<MonitoringEvent> caseProcessingType(final ProcessingType object) {
		return Set.of(new MonitorModelVisited<>(object));
	}

	@Override
	public Set<MonitoringEvent> caseStatisticalCharacterization(final StatisticalCharacterization object) {
		// TODO Auto-generated method stub
		return super.caseStatisticalCharacterization(object);
	}

	@Override
	public Set<MonitoringEvent> doSwitch(final EObject eObject) {
		final Set<MonitoringEvent> result = super.doSwitch(eObject);
		if (result == null) {
			return Collections.emptySet();
		} else {
			return result;
		}
	}



}
