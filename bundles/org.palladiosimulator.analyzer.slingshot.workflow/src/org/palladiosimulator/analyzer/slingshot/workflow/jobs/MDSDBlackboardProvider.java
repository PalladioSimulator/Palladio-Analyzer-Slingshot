package org.palladiosimulator.analyzer.slingshot.workflow.jobs;

import jakarta.inject.Provider;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class MDSDBlackboardProvider implements Provider<MDSDBlackboard> {

	private MDSDBlackboard blackboard;

	public void set(final MDSDBlackboard blackboard) {
		this.blackboard = blackboard;
	}

	@Override
	public MDSDBlackboard get() {
		return this.blackboard;
	}

}
