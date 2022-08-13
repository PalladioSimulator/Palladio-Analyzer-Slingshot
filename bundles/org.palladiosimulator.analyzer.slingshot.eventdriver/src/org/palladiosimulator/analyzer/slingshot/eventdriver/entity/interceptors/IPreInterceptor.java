package org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors;

import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;

import io.reactivex.rxjava3.functions.BiFunction;

public interface IPreInterceptor {
	
	public InterceptionResult apply(final InterceptorInformation preInterceptorInformation, final Object event);
	
}
