package org.palladiosimulator.analyzer.slingshot.monitor.ui;

import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.ui.events.ArchitectureModelsTabBuilderStarted;
import org.palladiosimulator.analyzer.slingshot.workflow.events.WorkflowLaunchConfigurationBuilderInitialized;
import org.palladiosimulator.monitorrepository.MonitorRepository;

@OnEvent(when = ArchitectureModelsTabBuilderStarted.class)
@OnEvent(when = WorkflowLaunchConfigurationBuilderInitialized.class)
public class MonitorRepositoryLaunchConfig implements SystemBehaviorExtension {

	private static final String FILE_NAME = "monitorrepository";
	
	@Subscribe
	public void onArchitectureModelsTab(final ArchitectureModelsTabBuilderStarted tab) {
		tab.newModelDefinition()
			 .fileName(FILE_NAME)
			 .modelClass(MonitorRepository.class)
			 .label("Monitor Repository")
			 .optional(true)
			 .build();
	}
	
	@Subscribe
	public void onWorkflowConfiguration(final WorkflowLaunchConfigurationBuilderInitialized init) {
		init.getConfiguration(FILE_NAME, 
				"monitorrepository", 
				(conf, model) -> conf.addOtherModelFile((String) model));
	}
	
}
