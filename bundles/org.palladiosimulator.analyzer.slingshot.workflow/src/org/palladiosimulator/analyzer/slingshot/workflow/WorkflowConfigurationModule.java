package org.palladiosimulator.analyzer.slingshot.workflow;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;


public class WorkflowConfigurationModule extends AbstractSlingshotExtension {
	
	static final SimuComConfigProvider simuComConfigProvider = new SimuComConfigProvider();
	
	public WorkflowConfigurationModule() {
	}

	@Override
	protected void configure() {
		bind(SimuComConfig.class).toProvider(simuComConfigProvider);
	}

	@Override
	public String getName() {
		return "Workflow Configuration";
	}
	
	
}
