package org.palladiosimulator.analyzer.slingshot.application;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.palladiosimulator.analyzer.slingshot.workflow.SimulationWorkflowConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.jobs.SimulationRootJob;
import org.palladiosimulator.experimentautomation.application.tooladapter.abstractsimulation.AbstractSimulationConfigFactory;
import org.palladiosimulator.experimentautomation.application.tooladapter.slingshot.model.SlingshotConfiguration;
import org.palladiosimulator.experimentautomation.application.tooladapter.slingshot.model.SlingshottooladapterPackage;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;
import org.palladiosimulator.experimentautomation.experiments.ExperimentsPackage;
import org.palladiosimulator.experimentautomation.experiments.InitialModel;

import de.uka.ipd.sdq.simucomframework.core.SimuComConfig;
import de.uka.ipd.sdq.workflow.BlackboardBasedWorkflow;
import de.uka.ipd.sdq.workflow.WorkflowFailedException;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

/**
 * Headless application for one regular Slingshot simulation run from an Experiment
 * Automation model.
 */
public class SlingshotApplication implements IApplication {

	private static final String APPLICATION_ARGUMENTS = "application.args";
	private static final String SLINGSHOT_ID = "org.palladiosimulator.slingshot";
	private static final String SIMULATE_LINKING_RESOURCES = "simulateLinkingResources";

	@Override
	public Object start(final IApplicationContext context) throws Exception {
		final Arguments arguments = Arguments.parse((String[]) context.getArguments().get(APPLICATION_ARGUMENTS));
		if (arguments.helpRequested) {
			System.out.println(Arguments.usage());
			return IApplication.EXIT_OK;
		}

		registerExperimentAutomationPackages();

		final List<Experiment> experiments = loadExperimentsFromFile(arguments.experimentsPath);
		final Experiment experiment = selectExperiment(experiments, arguments.experimentId);

		launchSimulation(experiment);
		return IApplication.EXIT_OK;
	}

	private static void registerExperimentAutomationPackages() {
		ExperimentsPackage.eINSTANCE.eClass();
		SlingshottooladapterPackage.eINSTANCE.eClass();
	}

	private static Experiment selectExperiment(final List<Experiment> experiments, final Optional<String> experimentId) {
		if (experimentId.isPresent()) {
			final String selectedId = experimentId.get();
			final Experiment selectedExperiment = experiments.stream()
					.filter(experiment -> selectedId.equals(experiment.getId()))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException(
							"No experiment with id \"" + selectedId + "\" exists in the experiments file."));

			getSlingshotConfiguration(selectedExperiment)
					.orElseThrow(() -> new IllegalArgumentException("Experiment \"" + selectedId
							+ "\" does not contain a SlingshotConfiguration from ExperimentAutomation PR #20."));
			return selectedExperiment;
		}

		return experiments.stream()
				.filter(experiment -> getSlingshotConfiguration(experiment).isPresent())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"No experiment with a SlingshotConfiguration from ExperimentAutomation PR #20 exists."));
	}

	private static Optional<SlingshotConfiguration> getSlingshotConfiguration(final Experiment experiment) {
		return experiment.getToolConfiguration()
				.stream()
				.filter(SlingshotConfiguration.class::isInstance)
				.map(SlingshotConfiguration.class::cast)
				.findFirst();
	}

	private static void launchSimulation(final Experiment experiment) {
		final SlingshotConfiguration slingshotConfiguration = getSlingshotConfiguration(experiment)
				.orElseThrow(() -> new IllegalArgumentException("Experiment \"" + experiment.getId()
						+ "\" does not contain a SlingshotConfiguration from ExperimentAutomation PR #20."));

		final Map<String, Object> configMap = AbstractSimulationConfigFactory.createConfigMap(experiment,
				slingshotConfiguration, SLINGSHOT_ID, List.of());
		configMap.put(SIMULATE_LINKING_RESOURCES, false);

		final SimuComConfig simuComConfig = new SimuComConfig(configMap, false);
		final SimulationWorkflowConfiguration workflowConfiguration = new SimulationWorkflowConfiguration(simuComConfig);
		setModelFilesInConfig(experiment, workflowConfiguration);

		final BlackboardBasedWorkflow<MDSDBlackboard> workflow = new BlackboardBasedWorkflow<>(
				new SimulationRootJob(workflowConfiguration, null), new MDSDBlackboard());

		try {
			workflow.execute(new NullProgressMonitor());
		} catch (JobFailedException | UserCanceledException e) {
			throw new WorkflowFailedException("Slingshot workflow failed", e);
		}
	}

	private static void setModelFilesInConfig(final Experiment experiment,
			final SimulationWorkflowConfiguration configuration) {
		final InitialModel models = experiment.getInitialModel();
		if (models == null) {
			throw new IllegalArgumentException("Experiment \"" + experiment.getId()
					+ "\" does not define an initial model.");
		}
		if (models.getAllocation() == null) {
			throw new IllegalArgumentException("Experiment \"" + experiment.getId()
					+ "\" does not define an allocation model.");
		}
		if (models.getUsageModel() == null) {
			throw new IllegalArgumentException("Experiment \"" + experiment.getId()
					+ "\" does not define a usage model.");
		}

		final Set<String> otherModelFiles = new LinkedHashSet<>();

		consumeModelLocation(models.getAllocation(), location -> configuration.setAllocationFiles(List.of(location)));
		consumeModelLocation(models.getUsageModel(), configuration::setUsageModelFile);

		collectModelLocation(models.getRepository(), otherModelFiles);
		collectModelLocation(models.getSystem(), otherModelFiles);
		collectModelLocation(models.getResourceEnvironment(), otherModelFiles);
		collectModelLocation(models.getMiddlewareRepository(), otherModelFiles);
		collectModelLocation(models.getEventMiddleWareRepository(), otherModelFiles);
		collectModelLocation(models.getScalingDefinitions(), otherModelFiles);
		collectModelLocation(models.getSpdSemanticConfiguration(), otherModelFiles);
		collectModelLocation(models.getMonitorRepository(), otherModelFiles);
		collectModelLocation(models.getServiceLevelObjectives(), otherModelFiles);
		collectModelLocation(models.getUsageEvolution(), otherModelFiles);

		otherModelFiles.forEach(configuration::addOtherModelFile);
	}

	private static void collectModelLocation(final EObject model, final Set<String> locations) {
		consumeModelLocation(model, locations::add);
	}

	private static void consumeModelLocation(final EObject model, final Consumer<String> consumer) {
		if (model == null) {
			return;
		}
		consumer.accept(getModelLocation(model));
	}

	private static String getModelLocation(final EObject model) {
		final Resource resource = model.eResource();
		if (resource != null) {
			return resource.getURI().toString();
		}
		if (model.eIsProxy()) {
			final URI proxyUri = ((InternalEObject) model).eProxyURI();
			if (proxyUri != null) {
				return proxyUri.trimFragment().toString();
			}
		}
		throw new IllegalArgumentException("Could not determine resource URI for model " + model);
	}

	private static List<Experiment> loadExperimentsFromFile(final IPath modelLocation) {
		final URI modelUri = URI.createFileURI(modelLocation.toOSString());

		final Resource resource;
		try {
			resource = new ResourceSetImpl().getResource(modelUri, true);
		} catch (final RuntimeException e) {
			throw new IllegalArgumentException("Could not load experiments file at \"" + modelLocation
					+ "\". Did you specify the correct file?", e);
		}

		if (resource.getContents().isEmpty()) {
			throw new IllegalStateException("Experiments file at \"" + modelLocation + "\" is empty.");
		}

		final EObject root = resource.getContents().get(0);
		if (ExperimentsPackage.eINSTANCE.getExperimentRepository().isInstance(root)) {
			return ((ExperimentRepository) root).getExperiments();
		}
		throw new IllegalArgumentException("The root element of \"" + modelLocation + "\" must be "
				+ ExperimentsPackage.eINSTANCE.getExperimentRepository().getName() + ", but was "
				+ root.getClass().getName() + ".");
	}

	@Override
	public void stop() {
		// Nothing to stop.
	}

	private static final class Arguments {
		private final IPath experimentsPath;
		private final Optional<String> experimentId;
		private final boolean helpRequested;

		private Arguments(final IPath experimentsPath, final Optional<String> experimentId,
				final boolean helpRequested) {
			this.experimentsPath = experimentsPath;
			this.experimentId = experimentId;
			this.helpRequested = helpRequested;
		}

		private static Arguments parse(final String[] applicationArguments) {
			final List<String> args = applicationArguments == null ? List.of() : Arrays.asList(applicationArguments);
			if (args.stream().anyMatch(Arguments::isHelpOption)) {
				return new Arguments(null, Optional.empty(), true);
			}

			File experimentsFile = null;
			String experimentId = null;
			final List<String> positionalArguments = new ArrayList<>();

			for (int index = 0; index < args.size(); index++) {
				final String current = args.get(index);
				switch (current) {
				case "-experiments":
					experimentsFile = parseExperimentsFile(requireValue(args, ++index, current), current);
					break;
				case "-experimentId":
					experimentId = requireValue(args, ++index, current);
					break;
				default:
					if (current.startsWith("-")) {
						throw new IllegalArgumentException("Unknown option \"" + current + "\".\n" + usage());
					}
					positionalArguments.add(current);
					break;
				}
			}

			if (experimentsFile == null) {
				if (positionalArguments.size() == 1) {
					experimentsFile = parseExperimentsFile(positionalArguments.get(0), "experiments file");
				} else {
					throw new IllegalArgumentException("Missing required experiments file.\n" + usage());
				}
			} else if (!positionalArguments.isEmpty()) {
				throw new IllegalArgumentException("Unexpected positional arguments: " + positionalArguments + "\n"
						+ usage());
			}

			return new Arguments(new Path(experimentsFile.getAbsolutePath()), Optional.ofNullable(experimentId), false);
		}

		private static boolean isHelpOption(final String arg) {
			return "-help".equals(arg) || "--help".equals(arg) || "-h".equals(arg);
		}

		private static String requireValue(final List<String> args, final int valueIndex, final String option) {
			if (valueIndex >= args.size() || args.get(valueIndex).startsWith("-")) {
				throw new IllegalArgumentException("Missing value for " + option + ".\n" + usage());
			}
			return args.get(valueIndex);
		}

		private static File parseExperimentsFile(final String value, final String argumentName) {
			final File file = new File(value);
			if (!file.isAbsolute()) {
				throw new IllegalArgumentException("The " + argumentName + " path must be absolute, but was \""
						+ value + "\".");
			}
			if (!file.exists() || !file.isFile()) {
				throw new IllegalArgumentException("The " + argumentName
						+ " path must point to an existing .experiments file, but was \"" + value + "\".");
			}
			if (!file.getName().endsWith(".experiments")) {
				throw new IllegalArgumentException("The " + argumentName + " path must end with .experiments, but was \""
						+ value + "\".");
			}
			return file;
		}

		private static String usage() {
			return "Usage: -experiments /absolute/path/model.experiments [-experimentId id]\n"
					+ "       /absolute/path/model.experiments [-experimentId id]";
		}
	}
}
