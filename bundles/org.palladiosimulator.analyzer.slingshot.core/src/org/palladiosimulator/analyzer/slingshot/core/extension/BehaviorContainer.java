package org.palladiosimulator.analyzer.slingshot.core.extension;

import java.util.List;

import com.google.inject.AbstractModule;

public final class BehaviorContainer extends AbstractModule {

	private AbstractSlingshotExtension extension;
	
	public BehaviorContainer(final AbstractSlingshotExtension extension) {
		this.extension = extension;
	}
	
	@Override
	public void configure() {
		this.extension.getBehaviorExtensions().forEach(this::bind);
	}
	
	public List<Class<? extends SimulationBehaviorExtension>> getExtensions() {
		return this.extension.getBehaviorExtensions();
	}
}
