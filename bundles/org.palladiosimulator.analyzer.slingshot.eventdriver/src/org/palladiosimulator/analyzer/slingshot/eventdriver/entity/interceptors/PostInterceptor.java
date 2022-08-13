package org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public class PostInterceptor implements IPostInterceptor {

	private final Method postInterceptorMethod;
	private final Object target;
	private final Class<?> forEvent;
	
	public PostInterceptor(Method postInterceptorMethod, Object target) {
		this.postInterceptorMethod = checkMethod(postInterceptorMethod);
		this.forEvent = postInterceptorMethod.getParameterTypes()[1];
		this.target = Objects.requireNonNull(target);
	}
	
	private static Method checkMethod(final Method postInterceptorMethod) {
		Objects.requireNonNull(postInterceptorMethod);
		
		final Class<?>[] params = postInterceptorMethod.getParameterTypes();
		if (params.length != 3) {
			throw new IllegalArgumentException("Post interceptor needs exactly three parameters: InterceptionInformation, the Event Type and Result");
		}
		
		if (!params[0].equals(InterceptorInformation.class) || !params[2].equals(Result.class)) {
			throw new IllegalArgumentException("Post-interceptors first argument must be of type InterceptionInformation, and its third argument must be of type Result");
		}
		
		if (!postInterceptorMethod.getReturnType().equals(InterceptionResult.class)) {
			throw new IllegalArgumentException("Post-interceptors return type must be InterceptionResult");
		}
		
		return postInterceptorMethod;
	}
	
	public Class<?> forEvent() {
		return this.forEvent;
	}
	
	@Override
	public InterceptionResult apply(InterceptorInformation inf, Object event, Result result) {
		try {
			return (InterceptionResult) postInterceptorMethod.invoke(target, inf, event, result);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return InterceptionResult.error(e);
		}
	}

}
