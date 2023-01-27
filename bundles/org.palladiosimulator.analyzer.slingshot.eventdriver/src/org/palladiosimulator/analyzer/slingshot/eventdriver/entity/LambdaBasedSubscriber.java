package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import java.util.Objects;
import java.util.function.Function;

import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public final class LambdaBasedSubscriber<T> extends AbstractSubscriber<T> {

	private final Function<? super T, ? extends Result<?>> handler;
	
	public LambdaBasedSubscriber(
			final int priority, 
			final Class<?>[] reifiedClasses,
			final IPreInterceptor preInterceptor,
			final IPostInterceptor postInterceptor,
			final Function<? super T, ? extends Result<?>> handler) {
		super(priority, reifiedClasses, preInterceptor, postInterceptor);
		this.handler = Objects.requireNonNull(handler, "A handler must not be null!");
	}

	@Override
	protected Result<?> acceptEvent(T event) throws Exception {
		return handler.apply(event);
	}

	@Override
	protected void release() {
		// Do nothing
	}
	
	@Override
	protected InterceptorInformation getInterceptorInformation() {
		return null; // TODO
	}
}
