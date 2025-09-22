package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import java.util.function.Function;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.probeframework.measurement.RequestContext;

public interface EventDistinguisher extends Function<DESEvent, RequestContext> {

	public static final EventDistinguisher DEFAULT_DISTINGUISHER = event -> RequestContext.EMPTY_REQUEST_CONTEXT;
	
}
