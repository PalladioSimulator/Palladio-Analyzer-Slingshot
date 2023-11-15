package org.palladiosimulator.analyzer.slingshot.workflow;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.analyzer.slingshot.workflow.jobs.MDSDBlackboardProvider;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class WorkflowConfigurationModule extends AbstractSlingshotExtension {

	public static final SimuComConfigProvider simuComConfigProvider = new SimuComConfigProvider();
	public static final MDSDBlackboardProvider blackboardProvider = new MDSDBlackboardProvider();

	public WorkflowConfigurationModule() {
	}

	@Override
	protected void configure() {
		bind(SimuComConfig.class).toProvider(simuComConfigProvider);
		bind(MDSDBlackboard.class).toProvider(blackboardProvider);
	}

	@Override
	public String getName() {
		return "Workflow Configuration";
	}
}
