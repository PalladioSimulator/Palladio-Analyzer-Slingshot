package org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Result {
	
	private Set<Object> resultEvents;
	
	private Result(final Collection<?> resultEvents) {
		if (resultEvents != null) {
			this.resultEvents = new HashSet<>(resultEvents);
		} else {
			this.resultEvents = Collections.emptySet();
		}
	}
	
	public Set<Object> getResultEvents() {
		return Collections.unmodifiableSet(this.resultEvents);
	}
	
	public static Result of(final Object... events) {
		return Result.of(Arrays.asList(events));
	}
	
	public static Result from(final Collection<?> resultEvents) {
		return new Result(resultEvents);
	}
	
	public static Result empty() {
		return new Result(null);
	}
}
