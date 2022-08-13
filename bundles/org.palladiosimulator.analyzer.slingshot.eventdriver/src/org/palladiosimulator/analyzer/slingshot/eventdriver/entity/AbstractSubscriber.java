package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

public abstract class AbstractSubscriber<T> implements Consumer<T>, Disposable, Comparable<AbstractSubscriber<T>> {
	
	private boolean disposed = false;
	private final int priority;
	
	public AbstractSubscriber() {
		this(0);
	}
	
	public AbstractSubscriber(final int priority) {
		if (priority < 0) {
			throw new IllegalArgumentException("Priority of a subscribe must be 0 or positive");
		}
		this.priority = priority;
	}
	
	@Override
	public void accept(final T event) {
		try {
			this.acceptEvent(event);
		} catch (final Exception e) {
			throw new RuntimeException("Could not dispose event", e);
		}
	}
	
	@Override
	public void dispose() {
		if (!disposed) {
			this.disposed = true;
			this.release();
		}
	}
	
	@Override
	public boolean isDisposed() {
		return this.disposed;
	}
	
	protected abstract void acceptEvent(final T event) throws Exception;
	
	protected abstract void release();
	
	@Override
	public int compareTo(final AbstractSubscriber<T> other) {
		return Integer.compare(other.priority, priority);
	}
	
	
}
