package org.palladiosimulator.analyzer.slingshot.core.extension;

import java.util.List;


import org.eclipse.emf.ecore.EObject;
import java.util.Collections;
import java.util.LinkedList;

import com.google.inject.AbstractModule;

public abstract class AbstractSlingshotExtension extends AbstractModule {
	
	private List<Class<? extends SimulationBehaviorExtension>> behaviorExtensions;
	
	protected final void install(final Class<? extends SimulationBehaviorExtension> behaviorExtension) {
		if (this.behaviorExtensions == null) {
			this.behaviorExtensions = new LinkedList<>();
		}
		
		System.out.println("Installing " + behaviorExtension.getSimpleName());
		this.behaviorExtensions.add(behaviorExtension);
	}
	
	protected final <T extends EObject> void provideModel(final Class<T> model, final Class<? extends ModelProvider<T>> provider) {
		bind(model).toProvider(provider);
	}
	
	@Override
	protected abstract void configure();
	
	public final List<Class<? extends SimulationBehaviorExtension>> getBehaviorExtensions() {
		if (this.behaviorExtensions == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(this.behaviorExtensions);
		}
	}
	
	public abstract String getName();
	
}
