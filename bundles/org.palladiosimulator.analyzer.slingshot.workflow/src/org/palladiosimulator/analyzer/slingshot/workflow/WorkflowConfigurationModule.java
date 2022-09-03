package org.palladiosimulator.analyzer.slingshot.workflow;

import java.util.Objects;

import com.google.inject.AbstractModule;

public class WorkflowConfigurationModule extends AbstractModule {
	
	private final SimulationWorkflowConfiguration workflowConfiguration;
	
	public WorkflowConfigurationModule(final SimulationWorkflowConfiguration workflowConfiguration) {
		this.workflowConfiguration = Objects.requireNonNull(workflowConfiguration);
	}

	@Override
	protected void configure() {
		bind(SimulationWorkflowConfiguration.class).toInstance(workflowConfiguration);
	}
	
	
}
