package org.palladiosimulator.analyzer.slingshot.monitor;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorFactoryProvider;
import org.palladiosimulator.analyzer.slingshot.monitor.interpreter.MonitorRepositoryInterpreterBehavior;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.ProbeFrameworkContextProvider;
import org.palladiosimulator.analyzer.slingshot.monitor.ui.MonitorRepositoryLaunchConfig;
import org.palladiosimulator.analyzer.slingshot.monitor.ui.MonitorRepositoryProvider;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

public class MonitorModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		// Behaviors
		install(MonitorRepositoryInterpreterBehavior.class);

		// Launch Config & Model File
		install(MonitorRepositoryLaunchConfig.class);
		provideModel(MonitorRepository.class, MonitorRepositoryProvider.class);

		// Further objects to be provided
		provideForDelay(IGenericCalculatorFactory.class, CalculatorFactoryProvider.class);
		provideForDelay(ProbeFrameworkContext.class, ProbeFrameworkContextProvider.class);
	}


}
