package org.palladiosimulator.analyzer.slingshot.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

public class SlingshotModule {
	
	private final Injector injector;
	
	public SlingshotModule(final Collection<Module> modules) {
		final List<Module> copied = new ArrayList<>(modules);
		copied.add(new SlingshotSystem());
		this.injector = Guice.createInjector(copied);
	}

	public <T> T getInstance(final Class<T> clazz) {
		return this.injector.getInstance(clazz);
	}
	
	public <T> T getInstance(final Key<T> key) {
		return this.injector.getInstance(key);
	}
	
	
}
