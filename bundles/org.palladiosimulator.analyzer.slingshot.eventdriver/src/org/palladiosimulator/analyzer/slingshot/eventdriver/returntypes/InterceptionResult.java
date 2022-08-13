package org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class InterceptionResult {
	
	private InterceptionResult() {}
	
	public abstract boolean wasSuccessful();
	
	public static Success success() {
		return new Success();
	}
	
	public static Abort abort() {
		return new Abort();
	}
	
	public static CompositeInterceptionResult compositeResult(final List<InterceptionResult> interceptionResults) {
		return new CompositeInterceptionResult(interceptionResults);
	}
	
	public static Error error(final Throwable throwable) {
		return new Error(throwable);
	}
	
	public static final class Success extends InterceptionResult {
		
		@Override
		public boolean wasSuccessful() {
			return true;
		}
		
	}
	
	public static final class Abort extends InterceptionResult {
		
		@Override
		public boolean wasSuccessful() {
			return false;
		}
		
	}
	
	public static final class Error extends InterceptionResult {
		
		private final Throwable error;
		
		public Error(final Throwable error) {
			this.error = Objects.requireNonNull(error);
		}
		
		@Override
		public boolean wasSuccessful() {
			return false;
		}
		
		public Throwable getError() {
			return this.error;
		}
	}
	
	public static final class CompositeInterceptionResult extends InterceptionResult {
		
		private final List<InterceptionResult> interceptionResults;
		private final boolean result;
		
		public CompositeInterceptionResult(final List<InterceptionResult> interceptionResult) {
			this.interceptionResults = Objects.requireNonNull(interceptionResult);
			this.result = interceptionResult.stream().allMatch(i -> i.wasSuccessful());
		}
		
		@Override
		public boolean wasSuccessful() {
			return this.result;
		}
		
		public List<InterceptionResult> getInterceptionResults() {
			return Collections.unmodifiableList(this.interceptionResults);
		}
	}
}
