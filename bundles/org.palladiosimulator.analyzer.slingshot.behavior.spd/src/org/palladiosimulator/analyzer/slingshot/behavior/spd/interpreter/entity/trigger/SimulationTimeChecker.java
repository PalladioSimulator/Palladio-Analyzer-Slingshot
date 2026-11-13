package org.palladiosimulator.analyzer.slingshot.behavior.spd.interpreter.entity.trigger;

import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.SimulationTimeReached;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.interpreter.entities.FilterObjectWrapper;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.interpreter.entities.FilterResult;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.spd.triggers.SimpleFireOnValue;
import org.palladiosimulator.spd.triggers.expectations.ExpectedTime;
import org.palladiosimulator.spd.triggers.stimuli.SimulationTime;

public final class SimulationTimeChecker extends TriggerChecker<SimulationTime> {

	/** The policy whose time events this checker reacts to; {@code null}: every time event. */
	private final String scalingPolicyId;

	public SimulationTimeChecker(final SimpleFireOnValue trigger) {
		this(trigger, null);
	}

	/**
	 * @param trigger         The trigger to check.
	 * @param scalingPolicyId The policy of the trigger: time events that another policy of the
	 *                        target group scheduled are disregarded.
	 */
	public SimulationTimeChecker(final SimpleFireOnValue trigger, final String scalingPolicyId) {
		super(trigger, SimulationTime.class, Set.of(ExpectedTime.class));
		this.scalingPolicyId = scalingPolicyId;
	}

	@Override
	public FilterResult doProcess(final FilterObjectWrapper objectWrapper) {
		final DESEvent event = objectWrapper.getEventToFilter();
		if (!(event instanceof SimulationTimeReached)) {
			return FilterResult.disregard("Expected simulation time reached event, but received" + event.getClass().getSimpleName());
		}
		
		final SimulationTimeReached simulationTimeReached = (SimulationTimeReached) event;
		if (this.scalingPolicyId != null && simulationTimeReached.getScalingPolicyId() != null
				&& !this.scalingPolicyId.equals(simulationTimeReached.getScalingPolicyId())) {
			/* A time policy hears only its own clock, not the instants of the other policies. */
			return FilterResult.disregard("The simulation time event belongs to another scaling policy.");
		}
		final ComparatorResult comparatorResult = this.compareToTrigger(simulationTimeReached.time());
		if (comparatorResult == ComparatorResult.IN_ACCORDANCE) {
			return FilterResult.success(event);
		} else {
			return FilterResult.disregard(String.format("Simulationtime of %f is not [%s] expectedValue. [[%s]]", simulationTimeReached.time(), ((SimpleFireOnValue) this.trigger).getRelationalOperator().toString(), comparatorResult.toString()));
		}

	}

}
