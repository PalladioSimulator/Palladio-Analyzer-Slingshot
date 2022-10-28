package org.palladiosimulator.analyzer.slingshot.core.extension;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;

@Singleton
public class PCMResourceSetPartitionProvider {
	
	private PCMResourceSetPartition pcmResourceSetPartition;

	public PCMResourceSetPartition get() {
		return this.pcmResourceSetPartition;
	}

	public void set(final PCMResourceSetPartition pcmResourceSetPartition) {
		this.pcmResourceSetPartition = pcmResourceSetPartition;
	}
	
}
