package org.palladiosimulator.analyzer.slingshot.core.extension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.inject.AbstractModule;

public abstract class AbstractSlingshotExtension extends AbstractModule {

	private List<Class<?>> behaviorExtensions;

	protected final void install(final Class<?> behaviorExtension) {
		if (this.behaviorExtensions == null) {
			this.behaviorExtensions = new LinkedList<>();
		}

		if (!this.behaviorExtensions.contains(behaviorExtension)) {
			System.out.println("Installing " + behaviorExtension.getSimpleName());
			this.behaviorExtensions.add(behaviorExtension);
		}
	}

	protected final <T extends EObject> void provideModel(final Class<T> model, final Class<? extends ModelProvider<T>> provider) {
		bind(model).toProvider(provider);
	}

	@Override
	protected abstract void configure();

	public final List<Class<?>> getBehaviorExtensions() {
		if (this.behaviorExtensions == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(this.behaviorExtensions);
		}
	}

	public String getName() {
		return getClass().getSimpleName();
	}

}
