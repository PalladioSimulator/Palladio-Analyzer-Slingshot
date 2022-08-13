package org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;

import io.reactivex.rxjava3.annotations.NonNull;

public class PreInterceptor implements IPreInterceptor {
	
	private final Method preInterceptorMethod;
	private final Object target;
	private final Class<?> forEvent;
	
	public PreInterceptor(final Method preInterceptorMethod, final Object target) {
		this.preInterceptorMethod = checkInterceptorMethod(preInterceptorMethod);
		this.forEvent = preInterceptorMethod.getParameterTypes()[1];
		this.target = target;
	}
	
	private static Method checkInterceptorMethod(final Method preInterceptorMethod) {
		Objects.requireNonNull(preInterceptorMethod);
		
		final Class<?>[] parameterTypes =  preInterceptorMethod.getParameterTypes();
		
		if (parameterTypes.length != 2) {
			throw new IllegalArgumentException("Interceptor Methods must have exactly two parameters: An PreInterceptorInformation and the actual event type");
		}
		
		if (!parameterTypes[0].equals(InterceptorInformation.class)) {
			throw new IllegalArgumentException("Interceptor's first parameter must be of PreInterceptorInformation<T> type");
		}
		
		final Class<?> returnType = preInterceptorMethod.getReturnType();
		if (!returnType.equals(InterceptionResult.class)) {
			throw new IllegalArgumentException("Interceptor's Return type must be InterceptionResult");
		}
		
		return preInterceptorMethod;
	}
	
	public Class<?> forEvent() {
		return this.forEvent;
	}

	@Override
	public @NonNull InterceptionResult apply(InterceptorInformation t, Object event) {
		InterceptionResult result;
		try {
			result = (InterceptionResult) this.preInterceptorMethod.invoke(this.target, t, event);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			result = InterceptionResult.error(e);
		}
		if (result == null) {
			result = InterceptionResult.error(new IllegalStateException("Interceptor must return a valid, non-null result"));
		}
		return result;
	}
}
