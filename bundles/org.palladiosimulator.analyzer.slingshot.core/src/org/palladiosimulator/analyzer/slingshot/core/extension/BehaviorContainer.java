package org.palladiosimulator.analyzer.slingshot.core.extension;

import java.util.List;

import com.google.inject.AbstractModule;

public final class BehaviorContainer extends AbstractModule {

	private final List<Class<? extends SimulationBehaviorExtension>> extensions;
	
	public BehaviorContainer(final AbstractSlingshotExtension extension) {
		this.extensions = extension.getBehaviorExtensions();
	}
	
	@Override
	public void configure() {
		this.extensions.forEach(this::bind);
	}
	
}
