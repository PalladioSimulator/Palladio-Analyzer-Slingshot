package org.palladiosimulator.analyzer.slingshot.core.api;

import java.util.function.Consumer;
import java.util.function.Function;

import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

/**
 * An interface that allows for adding handlers to a driver that uses
 * event bus.
 * 
 * @author Julijan Katic
 */
public interface BusBasedDriver {


	/**
	 * Registers an entire object that contain event handlers.
	 * 
	 * @param handlers An object that contains event handlers.
	 */
	public void registerHandlers(final SimulationBehaviorExtension handlers);
	
	/**
	 * Registers an event handler directly. The event handler can be a anonymous
	 * lambda function. 
	 * 
	 * @param <T> The event type itself.
	 * @param id An id that identifies this handler. This can be used to unregister again.
	 * @param forEvent The event type.
	 * @param handler The handler/subscriber.
	 * @see {@link #registerHandler(String, Class, Consumer)}
	 */
	public <T> void registerHandler(
			final String id,
			final Class<T> forEvent, 
			final Function<T, Result<?>> handler);
	
	/**
	 * Registers an event handler without a {@link Result}. This is similar to {@link #registerHandler(String, Class, Function)}.
	 * 
	 * @param <T> The event type itself.
	 * @param id An id that identifies this handler. This can be used to unregister again.
	 * @param forEvent The event type.
	 * @param handler The handler/subscriber.
	 * @see {@link #registerHandler(String, Class, Function)}
	 */
	default public <T> void registerHandler(
			final String id,
			final Class<T> forEvent,
			final Consumer<T> handler
			) {
		this.registerHandler(id, forEvent, event -> {
			handler.accept(event);
			return Result.empty();
		});
	}
}
