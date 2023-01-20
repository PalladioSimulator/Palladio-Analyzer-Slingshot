package org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A result is a container that holds any type of information of an event handler's result.
 * This could be, for example, new events that should be scheduled afterwards, especially
 * within the simulation phase.
 * 
 * The container itself is a set, that means there is no guarantee of order.
 * 
 * @author Julijan Katic
 *
 * @param <T> The containers type
 */
public final class Result<T> {
	
	private Set<T> resultEvents;
	
	private Result(final Collection<T> resultEvents) {
		if (resultEvents != null) {
			this.resultEvents = new HashSet<>(resultEvents);
			// Remove potentially nulls
			this.resultEvents.remove(null);
		} else {
			this.resultEvents = Collections.emptySet();
		}
	}
	
	public Set<Object> getResultEvents() {
		return Collections.unmodifiableSet(this.resultEvents);
	}
	
	public static <T> Result<T> of(final T... events) {
		return Result.from(Arrays.asList(events));
	}
	
	public static <T> Result<T> from(final Collection<T> resultEvents) {
		return new Result<>(resultEvents);
	}
	
	public static Result<?> empty() {
		return new Result<>(null);
	}
}
