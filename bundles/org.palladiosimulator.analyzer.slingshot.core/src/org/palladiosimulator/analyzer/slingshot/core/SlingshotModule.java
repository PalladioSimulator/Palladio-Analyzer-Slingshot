package org.palladiosimulator.analyzer.slingshot.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Provider;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorContainer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;

public final class SlingshotModule {
	
	private static final Logger LOGGER = Logger.getLogger(SlingshotModule.class);
	
	private final Injector injector;
	
	SlingshotModule() {
		final List<Module> copied = new ArrayList<>(Slingshot.getInstance().getExtensions());
		copied.add(new SlingshotSystem());
		copied.forEach(module -> LOGGER.debug("Following module added: " + module.getClass().getName()));
		this.injector = Guice.createInjector(copied);
	
		try {
			this.outputDependecyGraph("/home/julijan/git/slingshot-framework/graph.dot");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
