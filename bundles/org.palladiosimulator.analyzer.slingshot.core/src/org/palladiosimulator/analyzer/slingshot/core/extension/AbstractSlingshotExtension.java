package org.palladiosimulator.analyzer.slingshot.core.extension;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.inject.Provider;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import com.google.inject.AbstractModule;

public abstract class AbstractSlingshotExtension extends AbstractModule {

	private static final Logger LOGGER = LogManager.getLogger(AbstractSlingshotExtension.class);

	private List<Class<?>> behaviorExtensions;

	private Map<Class<?>, Class<? extends Provider<?>>> bindee2provider;

	protected final void install(final Class<?> behaviorExtension) {
		if (this.behaviorExtensions == null) {
			this.behaviorExtensions = new LinkedList<>();
		}

		if (!this.behaviorExtensions.contains(behaviorExtension)) {
			LOGGER.debug(String.format("Installing %s.", behaviorExtension.getSimpleName()));
			this.behaviorExtensions.add(behaviorExtension);
		} else {
			LOGGER.debug(String.format("Skip Installing %s, as it is already installed.",
					behaviorExtension.getSimpleName()));
		}
	}

	protected final <T extends EObject> void provideModel(final Class<T> model, final Class<? extends ModelProvider<T>> provider) {
		bind(model).toProvider(provider);
	}

	protected final <T extends Object> void install(final Class<T> bindee,
			final Class<? extends Provider<T>> provider) {
		if (this.bindee2provider == null) {
			this.bindee2provider = new HashMap<>();
		}

		bindee2provider.put(bindee, provider);
	}

	public final <T> Map<Class<T>, Class<? extends Provider<T>>> getBindee2provider() {
		final Map<Class<T>, Class<? extends Provider<T>>> rval = new HashMap<>();
		if (this.bindee2provider != null) {
			// somehow, i dont manage to cast the *entire* map at once.
			for (final Entry<Class<?>, Class<? extends Provider<?>>> entry : this.bindee2provider.entrySet()) {
				final Class<T> bindeee = (Class<T>) entry.getKey();
				final Class<? extends Provider<T>> provider = (Class<? extends Provider<T>>) entry.getValue();

				rval.put(bindeee, provider);
			}
		}
		return rval;
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
