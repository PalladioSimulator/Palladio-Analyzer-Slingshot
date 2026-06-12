package org.palladiosimulator.analyzer.slingshot.headless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.slingshot.workflow.WorkflowConfigurationModule;
import org.palladiosimulator.analyzer.workflow.core.ConstantsContainer;
import org.palladiosimulator.analyzer.workflow.core.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.analyzer.workflow.core.jobs.LoadModelIntoBlackboardJob;
import org.palladiosimulator.analyzer.workflow.core.jobs.PreparePCMBlackboardPartitionJob;

import de.uka.ipd.sdq.simucomframework.core.SimuComConfig;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class HeadlessApplication implements IApplication {

	private static final String ARG_ALLOCATION = "--allocation";
	private static final String ARG_SYSTEM = "--system";
	private static final String ARG_REPOSITORY = "--repository";
	private static final String ARG_RESOURCE_ENV = "--resourceenvironment";
	private static final String ARG_USAGE_MODEL = "--usagemodel";
	private static final String ARG_SIMULATION_TIME = "--simulationTime";
	private static final String ARG_MAX_MEASUREMENTS = "--maxMeasurements";
	private static final String ARG_SEED = "--seed";
	private static final String ARG_HELP = "--help";

	private SimulationDriver driver;

	@Override
	public Object start(final IApplicationContext context) throws Exception {
		final Map<?, ?> args = context.getArguments();
		final String[] appArgs = (String[]) args.get("application.args");

		final Map<String, String> parsed = parseArgs(appArgs != null ? appArgs : new String[0]);

		if (parsed.containsKey(ARG_HELP) || parsed.isEmpty()) {
			printHelp();
			return IApplication.EXIT_OK;
		}

		final List<String> modelFiles = new ArrayList<>();
		addIfPresent(parsed, ARG_ALLOCATION, modelFiles);
		addIfPresent(parsed, ARG_SYSTEM, modelFiles);
		addIfPresent(parsed, ARG_REPOSITORY, modelFiles);
		addIfPresent(parsed, ARG_RESOURCE_ENV, modelFiles);
		addIfPresent(parsed, ARG_USAGE_MODEL, modelFiles);

		if (modelFiles.isEmpty()) {
			System.err.println("Error: No model files provided.");
			printHelp();
			return Integer.valueOf(1);
		}

		final IProgressMonitor monitor = new NullProgressMonitor();

		final MDSDBlackboard blackboard = new MDSDBlackboard();
		final String partitionId = ConstantsContainer.DEFAULT_PCM_INSTANCE_PARTITION_ID;

		final PreparePCMBlackboardPartitionJob partitionJob = new PreparePCMBlackboardPartitionJob();
		partitionJob.setBlackboard(blackboard);
		partitionJob.execute(monitor);

		for (final String modelFile : modelFiles) {
			final LoadModelIntoBlackboardJob loadJob = new LoadModelIntoBlackboardJob(
					org.eclipse.emf.common.util.URI.createFileURI(modelFile), partitionId);
			loadJob.setBlackboard(blackboard);
			loadJob.execute(monitor);
		}

		final Map<String, Object> configMap = new HashMap<>();
		configMap.put(SimuComConfig.SIMULATION_TIME, parsed.getOrDefault(ARG_SIMULATION_TIME, "1000"));
		configMap.put(SimuComConfig.MAXIMUM_MEASUREMENT_COUNT,
				parsed.getOrDefault(ARG_MAX_MEASUREMENTS, "100000"));
		if (parsed.containsKey(ARG_SEED)) {
			configMap.put(SimuComConfig.USE_FIXED_SEED, true);
			configMap.put(SimuComConfig.SIMULATOR_ID, parsed.get(ARG_SEED));
		}
		final SimuComConfig config = new SimuComConfig(configMap, true);

		WorkflowConfigurationModule.simuComConfigProvider.set(config);
		WorkflowConfigurationModule.blackboardProvider.set(blackboard);

		final PCMResourceSetPartition partition = (PCMResourceSetPartition) blackboard
				.getPartition(partitionId);
		Slingshot.getInstance().getInstance(PCMResourceSetPartitionProvider.class).set(partition);

		this.driver = Slingshot.getInstance().getSimulationDriver();
		this.driver.init(config, monitor);
		this.driver.start();

		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		if (this.driver != null && this.driver.isRunning()) {
			this.driver.stop();
		}
	}

	private static Map<String, String> parseArgs(final String[] args) {
		final Map<String, String> result = new LinkedHashMap<>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
					result.put(args[i], args[i + 1]);
					i++;
				} else {
					result.put(args[i], "");
				}
			}
		}
		return result;
	}

	private static void addIfPresent(final Map<String, String> parsed, final String key,
			final List<String> target) {
		final String value = parsed.get(key);
		if (value != null && !value.isEmpty()) {
			target.add(value);
		}
	}

	private static void printHelp() {
		System.out.println("Slingshot Headless Simulation");
		System.out.println();
		System.out.println("Usage: slingshot-headless [options]");
		System.out.println();
		System.out.println("Required (at least one model file):");
		System.out.println("  --allocation <uri>             PCM allocation model");
		System.out.println("  --system <uri>                 PCM system model");
		System.out.println("  --repository <uri>             PCM repository model");
		System.out.println("  --resourceenvironment <uri>    PCM resource environment model");
		System.out.println("  --usagemodel <uri>             PCM usage model");
		System.out.println();
		System.out.println("Optional:");
		System.out.println("  --simulationTime <double>    Simulation end time (default: 1000)");
		System.out.println("  --maxMeasurements <int>      Max measurement count (default: 100000)");
		System.out.println("  --seed <long>                Fixed random seed");
		System.out.println("  --help                       Print this help");
		System.out.println();
		System.out.println("Example:");
		System.out.println(
				"  slingshot-headless --allocation model.allocation --repository model.repository --simulationTime 5000 --seed 42");
	}
}
