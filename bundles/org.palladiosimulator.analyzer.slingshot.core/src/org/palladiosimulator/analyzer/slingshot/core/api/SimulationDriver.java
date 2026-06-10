package org.palladiosimulator.analyzer.slingshot.core.api;

import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.common.snapshot.CompositeSimulationSnapshot;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;


import de.uka.ipd.sdq.simucomframework.core.SimuComConfig;

/**
 * Drives a single simulation run: initialization, event scheduling, and lifecycle control.
 */
public interface SimulationDriver extends SimulationScheduling {

	/**
	 * Initializes the simulation for a fresh run without a prior snapshot.
	 *
	 * @param config   the simulation configuration
	 * @param monitor  progress monitor for the workflow
	 */
	public void init(final SimuComConfig config, final IProgressMonitor monitor);

	/**
	 * Initializes the simulation, optionally from a captured snapshot.
	 *
	 * <p>
	 * When {@code initializationSnapshot} is present, the driver validates the snapshot
	 * against active snapshot-capable extensions during {@link #start()} and schedules
	 * {@link org.palladiosimulator.analyzer.slingshot.core.events.snapshot.SimulationStateInitializationRequested}
	 * before the regular pre-simulation bootstrap.
	 * </p>
	 *
	 * @param config                  the simulation configuration
	 * @param monitor                 progress monitor for the workflow
	 * @param initializationSnapshot  optional composite snapshot to initialize from
	 */
	public void init(final SimuComConfig config, final IProgressMonitor monitor,
			Optional<CompositeSimulationSnapshot> initializationSnapshot);

	public void start();

	public void stop();

	public boolean isRunning();
	
	public boolean isInitialized();
	
	public <T extends DESEvent> void registerEventHandler(final Subscriber<T> subscriber);

}
