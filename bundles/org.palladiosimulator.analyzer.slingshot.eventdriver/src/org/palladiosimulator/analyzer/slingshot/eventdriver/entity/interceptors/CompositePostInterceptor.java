package org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public class CompositePostInterceptor implements IPostInterceptor {
	
	private final List<IPostInterceptor> postInterceptors;
	
	public CompositePostInterceptor(final List<IPostInterceptor> postInterceptors) {
		this.postInterceptors = Objects.requireNonNull(postInterceptors);
	}
	
	public CompositePostInterceptor() {
		this.postInterceptors = new LinkedList<>();
	}
	
	public void add(final IPostInterceptor postInterceptor) {
		this.postInterceptors.add(postInterceptor);
	}

	@Override
	public InterceptionResult apply(InterceptorInformation inf, Object event, Result result) {
		final List<InterceptionResult> interceptionResults = this.postInterceptors.stream()
				.map(postInterceptor -> postInterceptor.apply(inf, event, result))
				.collect(Collectors.toList());
		
		return InterceptionResult.compositeResult(interceptionResults);
	}

}
