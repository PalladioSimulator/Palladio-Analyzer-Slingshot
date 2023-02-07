package org.palladiosimulator.analyzer.slingshot.core.engine;

import org.apache.log4j.Logger;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Singleton;

import org.apache.log4j.LogManager;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationInformation;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

import umontreal.ssj.simevents.Event;
import umontreal.ssj.simevents.Simulator;

@Singleton
public class SimulationEngineSSJ implements SimulationEngine, SimulationInformation {
	
	private final Logger LOGGER = LogManager.getLogger(SimulationEngineSSJ.class);
	
	private final Bus eventBus = Bus.instance();
	private final Simulator simulator = new Simulator();
	
	private int cumulativeEvents = 0;
	private boolean isAcceptingEvents = false;
	
	@Override
	public void init() {
		simulator.init();
		this.isAcceptingEvents = true;
	}

	//@Override
//	public void scheduleEvent(DESEvent event) {
//		if (!this.isAcceptingEvents) {
//			return;
//		}
//		
//		if (event.time() > 0) {
//			this.scheduleEventAt(event, event.time());
//			return;
//		}
//		
//		final Event simulationEvent = new SSJEvent(event);
//		LOGGER.debug("Schedule event " + event.getName() + " with delay " + event.delay());
//		simulationEvent.schedule(event.delay());
//	}
//
//	//@Override
//	public void scheduleEventAt(DESEvent event, double simulationTime) {
//		if (!this.isAcceptingEvents) {
//			return;
//		}
//		
//		final Event simulationEvent = new SSJEvent(event);
//		simulationEvent.setTime(simulationTime + event.delay());
//		this.simulator.getEventList().add(simulationEvent);
//	}
	
	@Override
	public void schedule(final DESEvent event, final Consumer<DESEvent> onDispatch) {
		if (!this.isAcceptingEvents) {
			return; //or throw?
		}
		
		final Event simulationEvent = new Event(simulator) {
			@Override
			public void actions() {
				if (simulator().isStopped()) {
					return;
				}
				
				event.setTime(simulator().time());
				onDispatch.accept(event);
				
				cumulativeEvents++;
			}
		};
		
		
		if (event.time() > 0) {
			simulationEvent.setTime(event.time() + event.delay());
			this.simulator.getEventList().add(simulationEvent);
		} else {
			simulationEvent.schedule(event.delay());
		}
	}

	@Override
	public SimulationInformation getSimulationInformation() {
		return this;
	}

	@Override
	public void start() {
		simulator.start();
		this.eventBus.acceptEvents(true);
	}

	@Override
	public void stop() {
		simulator.stop();
		this.eventBus.acceptEvents(false);
		this.isAcceptingEvents = false;
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
		return this.cumulativeEvents;
	}
	
//	private final class SSJEvent extends Event {
//		
//		private final DESEvent event;
//		
//		private SSJEvent(final DESEvent correspondingEvent) {
//			super(simulator);
//			this.event = correspondingEvent;
//		}
//		
//		@Override
//		public void actions() {
//			if (this.simulator().isStopped()) {
//				return;
//			}
//			
//			LOGGER.info("Even dispatched at " + this.simulator().time() + ": " + this.event.getName() + "(" + this.event.getId() + ")");
//			
//			this.event.setTime(this.simulator().time());
//			eventBus.post(this.event);
//			cumulativeEvents++;
//		}
//		
//	}

}
