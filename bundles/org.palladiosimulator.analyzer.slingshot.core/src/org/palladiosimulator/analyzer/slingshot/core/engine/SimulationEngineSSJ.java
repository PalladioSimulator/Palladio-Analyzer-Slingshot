package org.palladiosimulator.analyzer.slingshot.core.engine;

import javax.inject.Singleton;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationInformation;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.debugger.translator.DebuggingEnabledEventBusFactory;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;

import umontreal.ssj.simevents.Event;
import umontreal.ssj.simevents.Simulator;

@Singleton
public class SimulationEngineSSJ implements SimulationEngine, SimulationInformation {

	private final Logger LOGGER = LogManager.getLogger(SimulationEngineSSJ.class);

	private final Bus eventBus;
	private final Simulator simulator = new Simulator();

	private int cumulativeEvents = 0;
	private boolean isAcceptingEvents = false;
	
	public SimulationEngineSSJ() {
		final String eventBusName = "SSJ-Eventbus";
		if (EventDebugSystem.isDebugEnabled()) {
			eventBus = DebuggingEnabledEventBusFactory.createBus(eventBusName);
		} else {
			eventBus = Bus.instance(eventBusName);
		}
	}

	@Override
	public void init() {
		simulator.init();
		isAcceptingEvents = true;
	}

	@Override
	public void scheduleEvent(final DESEvent event) {
		if (!isAcceptingEvents) {
			return;
		}

		if (event.time() > 0) {
			this.scheduleEventAt(event, event.time());
			return;
		}

		final Event simulationEvent = new SSJEvent(event);
		LOGGER.debug("Schedule event " + event.getName() + " with delay " + event.delay());
		simulationEvent.schedule(event.delay());
	}

	@Override
	public void scheduleEventAt(final DESEvent event, final double simulationTime) {
		if (!isAcceptingEvents) {
			return;
		}

		final Event simulationEvent = new SSJEvent(event);
		simulationEvent.setTime(simulationTime + event.delay());
		simulator.getEventList().add(simulationEvent);
	}

	@Override
	public SimulationInformation getSimulationInformation() {
		return this;
	}

	@Override
	public void start() {
		simulator.start();
		eventBus.acceptEvents(true);
	}

	@Override
	public void stop() {
		simulator.stop();
		eventBus.acceptEvents(false);
		isAcceptingEvents = false;
	}

	@Override
	public boolean isRunning() {
		return simulator.isSimulating();
	}

	@Override
	public double currentSimulationTime() {
		return simulator.time();
	}

	@Override
	public int consumedEvents() {
		return cumulativeEvents;
	}

	@Override
	public void registerEventListener(final SimulationBehaviorExtension guavaEventClass) {
		eventBus.register(guavaEventClass);
	}

	
	@Override
	public <T> void registerEventListener(final Subscriber<T> subscriber) {
		eventBus.register(subscriber);
	}

	private final class SSJEvent extends Event {

		private final DESEvent event;

		private SSJEvent(final DESEvent correspondingEvent) {
			super(simulator);
			event = correspondingEvent;
		}

		@Override
		public void actions() {
			if (this.simulator().isStopped()) {
				return;
			}

			LOGGER.info("Even dispatched at " + this.simulator().time() + ": " + event.getName() + "(" + event.getId() + ")");

			event.setTime(this.simulator().time());
			eventBus.post(event);
			cumulativeEvents++;
		}

	}
	
}
