package org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;

import io.reactivex.rxjava3.annotations.NonNull;

public class CompositePreInterceptor implements IPreInterceptor {

	private final List<IPreInterceptor> preInterceptors;
	
	public CompositePreInterceptor(final List<IPreInterceptor> preInterceptors) {
		this.preInterceptors = Objects.requireNonNull(preInterceptors);
	}
	
	public CompositePreInterceptor() {
		this.preInterceptors = new LinkedList<>();
	}
	
	public void add(final IPreInterceptor preInterceptor) {
		this.preInterceptors.add(preInterceptor);
	}
	
	@Override
	public @NonNull InterceptionResult apply(@NonNull InterceptorInformation preInterceptorResult, @NonNull Object event) {
		final List<InterceptionResult> interceptionResults = this.preInterceptors.stream()
				.map(preInterceptor -> preInterceptor.apply(preInterceptorResult, event))
				.collect(Collectors.toList());
		
		return InterceptionResult.compositeResult(interceptionResults);
	}

	
}
