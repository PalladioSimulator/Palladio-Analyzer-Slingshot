package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

public abstract class AbstractSubscriber<T> implements Consumer<T>, Disposable, Comparable<AbstractSubscriber<T>> {
	
	private boolean disposed = false;
	private final int priority;
	private final Class<?>[] reifiedClasses;
	
	public AbstractSubscriber(final Subscribe subscriberAnnotation) {
		this.priority = subscriberAnnotation.priority();
		this.reifiedClasses = subscriberAnnotation.reified();
	}
	
	@Override
	public void accept(final T event) throws Exception {
		this.acceptEvent(event);
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
	
	public int getPriority() {
		return this.priority;
	}
	
	public Class<?>[] getReifiedClasses() {
		return this.reifiedClasses;
	}
}
