package org.palladiosimulator.analyzer.slingshot.workflow.events;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSystemEvent;

public final class WorkflowLaunchConfigurationBuilderInitialized extends AbstractSystemEvent {

	private final ILaunchConfiguration launchConfiguration;
	private final PCMWorkflowConfiguration pcmWorkflowConfiguration;
	
	public WorkflowLaunchConfigurationBuilderInitialized(
			final ILaunchConfiguration launchConfiguration,
			final PCMWorkflowConfiguration pcmWorkflowConfiguration) {
		this.launchConfiguration = Objects.requireNonNull(launchConfiguration);
		this.pcmWorkflowConfiguration = Objects.requireNonNull(pcmWorkflowConfiguration);
	}
	
	public void getConfiguration(final String key, final String defaultStr, final BiConsumer<PCMWorkflowConfiguration, Object> toLaunchConfig) {
		try {
			final Object conf = launchConfiguration.getAttribute(key, defaultStr);
			toLaunchConfig.accept(pcmWorkflowConfiguration, conf);
		} catch (CoreException e) {
			
		}
	}
	
	public Map<String, Object> forEach() {
		try {
			return launchConfiguration.getAttributes();
		} catch (CoreException e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}
}
