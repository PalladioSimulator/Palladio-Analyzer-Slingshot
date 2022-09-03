package org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;

import io.reactivex.rxjava3.annotations.NonNull;

public class CompositePreInterceptor implements IPreInterceptor {

	private final Map<Class<?>, List<IPreInterceptor>> preInterceptors;
	
	public CompositePreInterceptor() {
		this.preInterceptors = new HashMap<>();
	}
	
	public void add(final Class<?> forEvent, final IPreInterceptor preInterceptor) {
		this.preInterceptors.computeIfAbsent(forEvent, event -> new LinkedList<>())
							.add(preInterceptor);
	}
	
	@Override
	public @NonNull InterceptionResult apply(@NonNull InterceptorInformation preInterceptorResult, @NonNull Object event) {
		final List<InterceptionResult> interceptionResults = this.preInterceptors
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey().isAssignableFrom(event.getClass()))
				.flatMap(entry -> entry.getValue().stream())
				.map(preInterceptor -> preInterceptor.apply(preInterceptorResult, event))
				.collect(Collectors.toList());
		
		return InterceptionResult.compositeResult(interceptionResults);
	}

	
}
