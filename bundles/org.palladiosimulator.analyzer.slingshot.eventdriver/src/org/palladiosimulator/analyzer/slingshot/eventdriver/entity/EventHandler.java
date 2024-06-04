package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

/**
 * The actual subscriber of an event of type {@code T} that should be listened
 * to and eventually called.
 *
 * @author Julijan Katic
 *
 * @param <T> The event type
 */
@FunctionalInterface
public interface EventHandler<T> {

	/**
	 * A method that will be called upon the event. The method is allowed to through any
	 * kind of exception, which will be caught by an interceptor and appropriately delegated
	 * to the appropriate exception handlers.
	 * <p>
	 * The method returns a {@link Result} of any kind which the publisher of this event can
	 * use.
	 *
	 *
	 * @param event The concrete event that was published
	 * @return A result that holds any kind of information.
	 * @throws Exception Everything that can be thrown.
	 */
	public Result<?> acceptEvent(final T event) throws Throwable;

}
