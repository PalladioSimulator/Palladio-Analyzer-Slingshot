package org.palladiosimulator.analyzer.slingshot.core;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationEngine;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * This is the central class where all the Slingshot modules are defined, and where
 * the initial {@link Injector} is defined.
 * 
 * @author Julijan Katic
 *
 */
public class SlingshotSystem extends AbstractModule {

	private final SimulationDriver driver;
	private final SimulationEngine engine;
	
	private SlingshotSystem(final Builder builder) {
		assert builder != null;
		this.driver = Objects.requireNonNull(builder.driver);
		this.engine = Objects.requireNonNull(builder.engine);
	}
	
	@Provides
	public SimulationDriver getDriver() {
		return this.driver;
	}
	
	@Provides
	public SimulationEngine getEngine() {
		return this.engine;
	}
	
	
	
	public static final class Builder {
		private SimulationDriver driver;
		private SimulationEngine engine;
		
		public Builder driver(final SimulationDriver driver) {
			this.driver = driver;
			return this;
		}
		
		public Builder engine(final SimulationEngine engine) {
			this.engine = engine;
			return this;
		}
		
		public SlingshotSystem build() {
			return new SlingshotSystem(this);
		}
	}
}
