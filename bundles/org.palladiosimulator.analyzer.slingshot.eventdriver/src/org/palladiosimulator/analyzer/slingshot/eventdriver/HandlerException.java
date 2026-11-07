package org.palladiosimulator.analyzer.slingshot.eventdriver;

import java.util.Objects;

/**
 * Thrown by {@link Bus#post(Object)} when a subscriber threw while handling the posted event.
 *
 * A throwing subscriber used to be disposed silently while the simulation went on without it,
 * so one bad request could freeze a stage for every later user and the run still "succeeded".
 * The bus now ends the delivery of that event by throwing this exception, which aborts the run
 * at the event where the handler failed.
 *
 * @author Niklas Krieger
 */
public final class HandlerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final transient Object event;

	/**
	 * @param event the event whose delivery failed.
	 * @param cause what the subscriber threw.
	 */
	public HandlerException(final Object event, final Throwable cause) {
		super(String.format("A handler of %s threw %s: %s", Objects.requireNonNull(event).getClass().getSimpleName(),
				cause.getClass().getSimpleName(), cause.getMessage()), cause);
		this.event = event;
	}

	/**
	 * @return the event whose delivery failed.
	 */
	public Object getEvent() {
		return this.event;
	}
}
