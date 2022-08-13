package org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors;

import java.lang.reflect.Method;

public final class InterceptorInformation {
	private final Object target;
	private final Method method;
	
	public InterceptorInformation(Object target, Method method) {
		super();
		this.target = target;
		this.method = method;
	}

	public Object getTarget() {
		return target;
	}

	public Method getMethod() {
		return method;
	}
	
}