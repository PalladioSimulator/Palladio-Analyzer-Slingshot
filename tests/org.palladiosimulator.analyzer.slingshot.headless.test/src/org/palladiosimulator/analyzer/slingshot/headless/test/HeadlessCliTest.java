package org.palladiosimulator.analyzer.slingshot.headless.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;

class HeadlessCliTest {

	private static String minimalModelPath;
	private static String usageModelOnlyPath;

	@BeforeAll
	static void setUp() {
		final File modelsDir = new File("models");
		if (modelsDir.exists()) {
			minimalModelPath = new File(modelsDir, "MinimalModel").getAbsolutePath();
			usageModelOnlyPath = new File(modelsDir, "usageModelOnly").getAbsolutePath();
		} else {
			final File fallback = new File(
					"tests/org.palladiosimulator.analyzer.slingshot.headless.test/models");
			minimalModelPath = new File(fallback, "MinimalModel").getAbsolutePath();
			usageModelOnlyPath = new File(fallback, "usageModelOnly").getAbsolutePath();
		}
	}

	@Test
	void modelDirectoriesExist() {
		assertTrue(new File(minimalModelPath).exists(),
				"MinimalModel directory should exist at: " + minimalModelPath);
		assertTrue(new File(usageModelOnlyPath).exists(),
				"usageModelOnly directory should exist at: " + usageModelOnlyPath);
	}

	@Test
	void slingshotInstanceIsAvailable() {
		assertNotNull(Slingshot.getInstance());
		final SimulationDriver driver = Slingshot.getInstance().getSimulationDriver();
		assertNotNull(driver);
	}

	@Test
	void minimalModelLoadsIntoBlackboard() {
		final HeadlessTestRun run = assertDoesNotThrow(() -> new HeadlessTestRun(minimalModelPath));
		assertTrue(run.modelsLoaded());
		assertNotNull(run.getPartition());
	}

	@Test
	void usageModelLoadsIntoBlackboard() {
		final HeadlessTestRun run = assertDoesNotThrow(() -> new HeadlessTestRun(usageModelOnlyPath));
		assertTrue(run.modelsLoaded());
		assertNotNull(run.getPartition());
	}
}
