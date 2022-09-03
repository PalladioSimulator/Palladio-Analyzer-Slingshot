package org.palladiosimulator.analyzer.slingshot.workflow.events;

import java.util.List;

public interface PCMWorkflowConfiguration {
	void setUsageModelFile(final String usageModelFile);
	void setAllocationFiles(final List<String> allocationFiles);
}
