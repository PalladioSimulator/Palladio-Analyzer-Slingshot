package org.palladiosimulator.analyzer.slingshot.core.behavior;

import javax.inject.Inject;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.core.exceptions.IllegalResultException;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.OnException;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.PostIntercept;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

@OnEvent(when = SimulationFinished.class)
public class CoreBehavior implements SimulationBehaviorExtension {
	
	private static final Logger LOGGER = LogManager.getLogger(CoreBehavior.class);
	private final SimulationDriver simulationDriver;
	
	@Inject
	public CoreBehavior(final SimulationDriver simulationDriver) {
		this.simulationDriver = simulationDriver;
	}

	@Subscribe
	public void onSimulationFinished(final SimulationFinished simulationFinished) {
		this.simulationDriver.stop();
	}
	
	@PostIntercept
	public InterceptionResult rescheduleNextEvents(final InterceptorInformation interceptionInformation, final DESEvent event, final Result result) {
		LOGGER.debug("call post interception from " + interceptionInformation.getMethod().getName());
		
		result.getResultEvents().forEach(nextEvent -> {
			LOGGER.debug("Result is " + nextEvent.getClass().getName());
			if (nextEvent instanceof DESEvent) {
				simulationDriver.scheduleEvent((DESEvent) nextEvent);
			} else {
				/* Within the simulation, we only allow results that have DESEvents */
				throw new IllegalResultException("The result container contains objects that are not DESEvents"
						+ ", but instead " + nextEvent.getClass().getName() + ".");
			}
		});
		
		return InterceptionResult.success();
	}
	
	@OnException
	public void onGenericException(final Exception exception) {
		LOGGER.error("A weird exception has occured: ", exception);
	}
	
}
