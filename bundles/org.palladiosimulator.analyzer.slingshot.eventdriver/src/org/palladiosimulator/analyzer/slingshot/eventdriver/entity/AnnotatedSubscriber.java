package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public final class AnnotatedSubscriber extends AbstractSubscriber<Object> {

	private final Method method;
	private final Object target;
	private final Class<?> resultType;
	
	public AnnotatedSubscriber(final Method method, final Object target, 
			final IPreInterceptor preInterceptor,
			final IPostInterceptor postInterceptor,
			final Subscribe subscriberAnnotation) {
		super(subscriberAnnotation.priority(), subscriberAnnotation.reified(), preInterceptor, postInterceptor);
		this.method = Objects.requireNonNull(method);
		this.target = Objects.requireNonNull(target);
		this.resultType = method.getReturnType();
	}

	@Override
	protected Result<?> acceptEvent(Object event) throws Exception {
	
		final Result<?> result;
		
		try {
			if (resultType.equals(void.class) || resultType.equals(Void.class)) {
				// Return type void is equivalent to Result.empty()
				result = Result.empty();
				this.method.invoke(target, event);
			} else {
				result = (Result) this.method.invoke(target, event);
			}
		} catch (final InvocationTargetException ex) {
			if (ex.getCause() != null && ex.getCause() instanceof Exception) {
				throw (Exception) ex.getCause();
			}
			return Result.empty(); // TODO: Look at this
		}
		
		return result;
		
	}
	
	@Override
	protected InterceptorInformation getInterceptorInformation() {
		return new InterceptorInformation(target, method);
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
