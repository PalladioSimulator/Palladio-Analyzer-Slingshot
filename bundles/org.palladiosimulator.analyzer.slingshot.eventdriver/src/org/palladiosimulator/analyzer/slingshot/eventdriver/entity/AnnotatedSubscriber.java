package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public final class AnnotatedSubscriber extends AbstractSubscriber<Object> {

	private final Method method;
	private final Object target;
	private final Optional<IPreInterceptor> preInterceptor;
	private final Optional<IPostInterceptor> postInterceptor;
	private final Class<?> resultType;
	
	public AnnotatedSubscriber(final Method method, 
			final Object target, 
			final IPreInterceptor preInterceptor,
			final IPostInterceptor postInterceptor) {
		this(method, target, preInterceptor, postInterceptor, 0);
	}
	
	public AnnotatedSubscriber(final Method method, final Object target, 
			final IPreInterceptor preInterceptor,
			final IPostInterceptor postInterceptor, final int priority) {
		super(priority);
		this.method = Objects.requireNonNull(method);
		this.target = Objects.requireNonNull(target);
		this.preInterceptor = Optional.ofNullable(preInterceptor);
		this.postInterceptor = Optional.ofNullable(postInterceptor);
		this.resultType = method.getReturnType();
	}

	@Override
	protected void acceptEvent(Object event) throws Exception {
		final InterceptorInformation preInterceptionInformation = new InterceptorInformation(target, method);
		
		final InterceptionResult preInterceptionResult = this.preInterceptor
				.map(preInterceptor -> preInterceptor.apply(preInterceptionInformation, event))
				.orElseGet(() -> InterceptionResult.success());
		
		final Result result;
		if (preInterceptionResult.wasSuccessful()) {
			if (resultType.equals(void.class) || resultType.equals(Void.class)) {
				// Return type void is equivalent to Result.empty()
				result = Result.empty();
				this.method.invoke(target, event);
			} else {
				result = (Result) this.method.invoke(target, event);
			}
		} else {
			result = Result.empty();
		}
		
		final InterceptionResult postInterceptionResult = this.postInterceptor
				.map(postInterceptor -> postInterceptor.apply(preInterceptionInformation, event, result))
				.orElseGet(() -> InterceptionResult.success());
	}

	@Override
	protected void release() {
		
	}

	@Override
	public int hashCode() {
		return Objects.hash(method, target);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnnotatedSubscriber other = (AnnotatedSubscriber) obj;
		return Objects.equals(method, other.method) && Objects.equals(target, other.target);
	}
	
	
	
}
