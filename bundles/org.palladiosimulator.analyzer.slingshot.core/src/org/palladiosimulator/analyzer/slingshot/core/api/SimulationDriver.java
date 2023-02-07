package org.palladiosimulator.analyzer.slingshot.core.api;

import org.eclipse.core.runtime.IProgressMonitor;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

/**
 * An interface describing (event bus) driver for the simulation run.
 * One is able to start and stop the simulation through this interface. However,
 * before the simulation is started, one needs to first initialize it with
 * {@link #init(SimuComConfig, IProgressMonitor)}
 * 
 * @author Julijan Katic
 */
public interface SimulationDriver extends SimulationScheduling, BusBasedDriver {
	
	/**
	 * Initializes the driver using the {@code SimuComConfig} configuration and the
	 * progress monitor. This can also be used to re-initialize the driver.
	 * 
	 * @param config The (launch) configuration for the simulation.
	 * @param monitor The monitor to describe the overall progress of the simulation.
	 */
	public void init(final SimuComConfig config, final IProgressMonitor monitor);
	
	/**
	 * Starts the simulation. Note that the driver must first be initialized.
	 * 
	 * @throws IllegalStateException if the driver wasn't initialized before.
	 * @see #init(SimuComConfig, IProgressMonitor)
	 * @see #isInitialized()
	 */
	public void start();
	
	/**
	 * Stops the simulation. It won't do anything if the simulation is not
	 * running.
	 */
	public void stop();
	
	/**
	 * Returns whether the simulation is currently running and accepting events.
	 * 
	 * @return True if and only if the simulation is running.
	 */
	public boolean isRunning();
	
	/**
	 * Shuts down the driver and de-initializes it. This means that all references
	 * are destroyed, and {@link #isInitialized()} will return {@code false} again
	 * from this point on. Thus, one needs to initialize the driver again to use it.
	 */
	public void shutdown();
	
	/**
	 * Returns whether the driver is now correctly initialized.
	 * @return
	 */
	public boolean isInitialized();
	
}
