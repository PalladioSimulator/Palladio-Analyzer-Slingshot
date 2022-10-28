package org.palladiosimulator.analyzer.slingshot.core.extension;

import java.util.List;
import java.util.function.Consumer;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;

public class AbstractModelModule extends AbstractModule {

	private final List<Consumer<AbstractModelModule>> binders;
	
	public AbstractModelModule(List<Consumer<AbstractModelModule>> binders) {
		this.binders = binders;
	}
	
	@Override
	public <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz) {
		return super.bind(clazz);
	}

	@Override
	protected void configure() {
		binders.forEach(consumer -> consumer.accept(this));
	}
		
}
