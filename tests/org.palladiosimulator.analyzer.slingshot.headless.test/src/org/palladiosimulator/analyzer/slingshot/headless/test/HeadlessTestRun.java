package org.palladiosimulator.analyzer.slingshot.headless.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.workflow.core.ConstantsContainer;
import org.palladiosimulator.analyzer.workflow.core.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.analyzer.workflow.core.jobs.LoadModelIntoBlackboardJob;
import org.palladiosimulator.analyzer.workflow.core.jobs.PreparePCMBlackboardPartitionJob;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class HeadlessTestRun {

	private final MDSDBlackboard blackboard;
	private final String partitionId;

	public HeadlessTestRun(final String modelDirPath) throws Exception {
		this.blackboard = new MDSDBlackboard();
		this.partitionId = ConstantsContainer.DEFAULT_PCM_INSTANCE_PARTITION_ID;

		final NullProgressMonitor monitor = new NullProgressMonitor();

		final PreparePCMBlackboardPartitionJob partitionJob = new PreparePCMBlackboardPartitionJob();
		partitionJob.setBlackboard(this.blackboard);
		partitionJob.execute(monitor);

		final File modelDir = new File(modelDirPath);
		final File[] modelFiles = modelDir.listFiles((dir, name) -> {
			final String lower = name.toLowerCase();
			return lower.endsWith(".allocation") || lower.endsWith(".repository")
					|| lower.endsWith(".system") || lower.endsWith(".resourceenvironment")
					|| lower.endsWith(".usagemodel");
		});

		if (modelFiles == null || modelFiles.length == 0) {
			throw new IllegalArgumentException("No PCM model files found in: " + modelDirPath);
		}

		for (final File modelFile : modelFiles) {
			final LoadModelIntoBlackboardJob loadJob = new LoadModelIntoBlackboardJob(
					URI.createFileURI(modelFile.getAbsolutePath()), this.partitionId);
			loadJob.setBlackboard(this.blackboard);
			loadJob.execute(monitor);
		}
	}

	public PCMResourceSetPartition getPartition() {
		return (PCMResourceSetPartition) this.blackboard.getPartition(this.partitionId);
	}

	public MDSDBlackboard getBlackboard() {
		return this.blackboard;
	}

	public boolean modelsLoaded() {
		return this.blackboard.getPartition(this.partitionId) != null;
	}
}
