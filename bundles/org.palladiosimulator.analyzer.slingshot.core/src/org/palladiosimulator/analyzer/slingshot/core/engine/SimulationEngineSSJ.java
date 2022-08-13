package org.palladiosimulator.analyzer.slingshot.core.engine;

import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationInformation;
import org.palladiosimulator.analyzer.slingshot.core.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;

import umontreal.ssj.simevents.Event;
import umontreal.ssj.simevents.Simulator;

public class SimulationEngineSSJ implements SimulationEngine, SimulationInformation {
	
	private final Bus eventBus = Bus.instance();
	private final Simulator simulator = new Simulator();
	
	private int cumutativeEvents = 0;
	
	@Override
	public void init() {
		simulator.init();
	}

	@Override
	public void scheduleEvent(DESEvent event) {
		if (event.time() > 0) {
			this.scheduleEventAt(event, event.time());
			return;
		}
		
		final Event simulationEvent = new SSJEvent(event);
		simulationEvent.schedule(event.delay());
	}

	@Override
	public void scheduleEventAt(DESEvent event, double simulationTime) {
		final Event simulationEvent = new SSJEvent(event);
		simulationEvent.setTime(simulationTime + event.delay());
		this.simulator.getEventList().add(simulationEvent);
	}

	@Override
	public SimulationInformation getSimulationInformation() {
		return this;
	}

	@Override
	public void start() {
		simulator.start();
	}

	@Override
	public void stop() {
		simulator.stop();
	}

	@Override
	public boolean isRunning() {
		return this.simulator.isSimulating();
	}

	@Override
	public double currentSimulationTime() {
		return this.simulator.time();
	}

	@Override
	public int consumedEvents() {
		return this.cumutativeEvents;
	}
	
	private final class SSJEvent extends Event {
		
		private final DESEvent event;
		
		private SSJEvent(final DESEvent correspondingEvent) {
			super(simulator);
			this.event = correspondingEvent;
		}
		
		@Override
		public void actions() {
			this.event.setTime(this.time());
			eventBus.post(this.event);
			cumutativeEvents++;
		}
		
	}

	@Override
	public void registerEventListener(SimulationBehaviorExtension guavaEventClass) {
		this.eventBus.register(guavaEventClass);
	}

}
