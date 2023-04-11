package org.palladiosimulator.analyzer.slingshot.core.api;

import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;

public interface SimulationEngine {

	public void init();

	public SimulationInformation getSimulationInformation();

	public void start();

	public void stop();

	public boolean isRunning();

	public void scheduleEvent(final DESEvent event);

	public void scheduleEventAt(final DESEvent event, final double simulationTime);

	public void registerEventListener(final SimulationBehaviorExtension guavaEventClass);

	public <T> void registerEventListener(final Subscriber<T> subscriber);

    public Set<DESEvent> getScheduledEvents();

}
