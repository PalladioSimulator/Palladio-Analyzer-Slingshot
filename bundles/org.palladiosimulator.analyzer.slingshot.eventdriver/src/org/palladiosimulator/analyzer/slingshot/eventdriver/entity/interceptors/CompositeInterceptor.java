package org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public class CompositeInterceptor implements IPreInterceptor, IPostInterceptor {

	private final Map<Class<?>, List<IPreInterceptor>> preInterceptors = new HashMap<>();
	private final Map<Class<?>, List<IPostInterceptor>> postInterceptors = new HashMap<>();
	
	public void add(final Class<?> forEvent, final IPreInterceptor preInterceptor) {
		this.preInterceptors.computeIfAbsent(forEvent, event -> new LinkedList<>())
						    .add(preInterceptor);
	}
	
	public void add(final Class<?> forEvent, final IPostInterceptor postInterceptor) {
		this.postInterceptors.computeIfAbsent(forEvent, event -> new LinkedList<>())
						     .add(postInterceptor);
	}
	
	@Override
	public InterceptionResult apply(InterceptorInformation inf, Object event, Result result) {
		final List<InterceptionResult> interceptionResults = this.postInterceptors
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey().isAssignableFrom(event.getClass()))
				.flatMap(entry -> entry.getValue().stream())
				.map(preInterceptor -> preInterceptor.apply(inf, event, result))
				.collect(Collectors.toList());
		
		return InterceptionResult.compositeResult(interceptionResults);
	}

	@Override
	public InterceptionResult apply(InterceptorInformation inf, Object event) {
		final List<InterceptionResult> interceptionResults = this.preInterceptors
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey().isAssignableFrom(event.getClass()))
				.flatMap(entry -> entry.getValue().stream())
				.map(preInterceptor -> preInterceptor.apply(inf, event))
				.collect(Collectors.toList());
		
		return InterceptionResult.compositeResult(interceptionResults);
	}
	
}
