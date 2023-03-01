package org.palladiosimulator.analyzer.slingshot.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Provider;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;

public final class InjectorHolder {

	private static final Logger LOGGER = Logger.getLogger(InjectorHolder.class);

	private final Injector injector;

	InjectorHolder() {
		final List<Module> copied = new ArrayList<>(Slingshot.getInstance().getSystemExtensions());
		copied.add(new SlingshotModule());
		copied.forEach(module -> LOGGER.debug("Following module added: " + module.getClass().getName()));
		this.injector = Guice.createInjector(copied);
	}

	public <T> T getInstance(final Class<T> clazz) {
		return this.injector.getInstance(clazz);
	}

	public <T> T getInstance(final Key<T> key) {
		return this.injector.getInstance(key);
	}

	public <T> Provider<T> getProvider(final Class<T> clazz) {
		return this.injector.getProvider(clazz);
	}

	public void outputDependecyGraph(final String fileName) throws IOException {
		final PrintWriter out = new PrintWriter(new File(fileName), "UTF-8");

		final Injector graphInjector = Guice.createInjector(new GraphvizModule());
		final GraphvizGrapher renderer = graphInjector.getInstance(GraphvizGrapher.class);
		renderer.setOut(out);
		renderer.setRankdir("TB");
		renderer.graph(this.injector);
	}
}
