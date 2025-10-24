package org.palladiosimulator.analyzer.slingshot.workflow;

import java.util.LinkedList;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.core.api.SimulationConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.events.PCMWorkflowConfiguration;
import org.palladiosimulator.analyzer.workflow.core.configurations.AbstractPCMWorkflowRunConfiguration;

import de.uka.ipd.sdq.simucomframework.core.SimuComConfig;

public class SimulationWorkflowConfiguration extends AbstractPCMWorkflowRunConfiguration implements PCMWorkflowConfiguration, SimulationConfiguration {
	
	private final SimuComConfig simuComConfig;
	private List<String> otherFiles;
	
	public SimulationWorkflowConfiguration(final SimuComConfig configuration) {
		this.simuComConfig = configuration;
	}
	
	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaults() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public SimuComConfig getSimuComConfig() {
		return this.simuComConfig;
	}

	@Override
	public List<String> getPCMModelFiles() {
		final List<String> files = super.getPCMModelFiles();
		if (this.otherFiles != null) {
			files.addAll(this.otherFiles);
		}
		return files;
	}
	
	@Override
	public void addOtherModelFile(String modelFile) {
		if (this.otherFiles == null) {
			this.otherFiles = new LinkedList<>();
		}
		this.otherFiles.add(modelFile);
	}
}
