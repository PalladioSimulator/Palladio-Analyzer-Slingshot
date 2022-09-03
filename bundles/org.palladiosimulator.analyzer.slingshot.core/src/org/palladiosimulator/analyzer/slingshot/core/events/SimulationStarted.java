package org.palladiosimulator.analyzer.slingshot.core.events;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.PublishableBy;

@PublishableBy(bundles = Slingshot.BUNDLE_ID)
public class SimulationStarted extends AbstractSimulationEvent {

}
