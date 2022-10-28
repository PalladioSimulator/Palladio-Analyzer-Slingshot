package org.palladiosimulator.analyzer.slingshot.workflow;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;

import com.google.inject.AbstractModule;

public class WorkflowConfigurationModule extends AbstractSlingshotExtension {
	
	public WorkflowConfigurationModule() {
	}

	@Override
	protected void configure() {
		//bind(PCMResourceSetPartitionProvider.class);
	}

	@Override
	public String getName() {
		return "Workflow Configuration";
	}
	
	
}
