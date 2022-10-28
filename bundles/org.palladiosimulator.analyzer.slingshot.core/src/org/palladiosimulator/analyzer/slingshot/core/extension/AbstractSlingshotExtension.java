package org.palladiosimulator.analyzer.slingshot.core.extension;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Provider;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;

import java.util.Collections;
import java.util.LinkedList;

import com.google.inject.AbstractModule;

public abstract class AbstractSlingshotExtension extends AbstractModule {
	
	private List<Class<?>> behaviorExtensions;
	private List<Consumer<AbstractModelModule>> modelProviders;
	
	protected final void install(final Class<?> behaviorExtension) {
		if (this.behaviorExtensions == null) {
			this.behaviorExtensions = new LinkedList<>();
		}
		
		System.out.println("Installing " + behaviorExtension.getSimpleName());
		this.behaviorExtensions.add(behaviorExtension);
	}
	
	protected final <T extends EObject> void provideModel(final Class<T> model, final Class<? extends ModelProvider<T>> provider) {
		bind(model).toProvider(provider);
		//if (modelProviders == null) {
		//	modelProviders = new LinkedList<>();
		//}
		
		//this.modelProviders.add(module -> module.bind(model).toProvider(provider));
	}
	
//	protected final <T extends EObject> void provideModel(final Class<T> model, final Function<PCMResourceSetPartition, T> provider) {
//		bind(model).toProvider(new Provider<T>() {
//
//			@Override
//			public T get() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//		});
//	}
	
	@Override
	protected abstract void configure();
	
	public final List<Class<?>> getBehaviorExtensions() {
		if (this.behaviorExtensions == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(this.behaviorExtensions);
		}
	}
	
	public abstract String getName();
	
}
