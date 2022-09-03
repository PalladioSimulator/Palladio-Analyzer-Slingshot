package org.palladiosimulator.analyzer.slingshot.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.palladiosimulator.analyzer.slingshot.ui.tabs.SimulationArchitectureModelsTab;

import de.uka.ipd.sdq.codegen.simucontroller.runconfig.SimuComConfigurationTab;
import de.uka.ipd.sdq.codegen.simucontroller.runconfig.SimuConfigurationTab;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.DebugEnabledCommonTab;

public class SlingshotLaunchSimulationTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		final ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new SimulationArchitectureModelsTab(),
				new SimuComConfigurationTab(),
				new SimuConfigurationTab(),
				new DebugEnabledCommonTab()
		};
		
		super.setTabs(tabs);
	}

	
}
