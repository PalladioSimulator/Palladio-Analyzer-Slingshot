package org.palladiosimulator.analyzer.slingshot.behavior.spd.data;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;

/* 
 * TODO: Currently, it could happen that multiple events are spawned at the same
 * 		 simulation time, which will affect performance. Look at how only one event
 * 		 for a certain simulation time is spawned at the same time ==> Then, no target
 * 		 group checking is needed in this case.
 */
/**
 * An event that is scheduled at an exact simulation time. In context of SPD,
 * this is needed especially for triggers with simulation time stimulus.
 * 
 * This event also carries a necessary target group (identifier) in order to
 * correctly identify whether this event belongs to the right scaling policy.
 * 
 * @author Julijan Katic
 */
public final class SimulationTimeReached extends AbstractSimulationEvent implements SpdBasedEvent {

	private final String targetGroupId;
	private final String scalingPolicyId;

	/**
	 * Constructs a new simulation time reached event that is scheduled at exactly {@code simulationTime} and a
	 * possibly a {@code delay}.
	 * 
	 * The constructed event should then be scheduled AFTER the current simulation time. If the specified simulation time
	 * is in the past, an exception at the event bus might occur.
	 * 
	 * @param targetGroupId The target group identifier this event belongs to. 
	 * @param simulationTime A positive number that specifies the exact simulation time that should be reached.
	 * @param delay A non-negative number specifying a delay after the simulation time.
	 */
	public SimulationTimeReached(final String targetGroupId, final double simulationTime, final double delay) {
		this(targetGroupId, null, simulationTime, delay);
	}
	
	public SimulationTimeReached(final String targetGroupId, final double simulationTime) {
		this(targetGroupId, simulationTime, 0);
	}

	/**
	 * Constructs a simulation time reached event that belongs to one scaling policy: only that
	 * policy's time trigger reacts to it, the other policies of the target group do not.
	 *
	 * @param targetGroupId   The target group identifier this event belongs to.
	 * @param scalingPolicyId The scaling policy whose trigger scheduled this event, or
	 *                        {@code null} for an event every policy of the group receives.
	 * @param simulationTime  A positive number that specifies the exact simulation time that
	 *                        should be reached.
	 * @param delay           A non-negative number specifying a delay after the simulation time.
	 */
	public SimulationTimeReached(final String targetGroupId, final String scalingPolicyId,
			final double simulationTime, final double delay) {
		super(delay);
		this.targetGroupId = targetGroupId;
		this.scalingPolicyId = scalingPolicyId;
		this.setTime(simulationTime);
	}

	public String getTargetGroupId() {
		return targetGroupId;
	}

	/**
	 * @return The scaling policy this event belongs to, or {@code null} if it belongs to every
	 *         policy of its target group.
	 */
	public String getScalingPolicyId() {
		return scalingPolicyId;
	}
	
	
}
