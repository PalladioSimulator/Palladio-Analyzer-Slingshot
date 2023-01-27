package org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors;

import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

@FunctionalInterface
public interface IPostInterceptor {

	public InterceptionResult apply(final InterceptorInformation inf, final Object event, final Result result);
	
}
