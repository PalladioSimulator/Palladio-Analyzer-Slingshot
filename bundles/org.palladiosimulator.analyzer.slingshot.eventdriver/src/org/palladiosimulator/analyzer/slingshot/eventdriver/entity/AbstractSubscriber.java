package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * 
 * @author julijankatic
 *
 * @param <T> The "events" or "messages" it allows.
 */
public abstract class AbstractSubscriber<T> implements Consumer<T>, Disposable, Comparable<AbstractSubscriber<T>> {
	
	private boolean disposed = false;
	private final int priority;
	private final Class<?>[] reifiedClasses;

	private final Optional<IPreInterceptor> preInterceptor;
	private final Optional<IPostInterceptor> postInterceptor;
	

	public AbstractSubscriber(int priority, Class<?>[] reifiedClasses, final IPreInterceptor preInterceptor,
			final IPostInterceptor postInterceptor) {
		super();
		this.priority = priority;
		this.reifiedClasses = reifiedClasses;
		this.preInterceptor = Optional.ofNullable(preInterceptor);
		this.postInterceptor = Optional.ofNullable(postInterceptor);
	}

	@Override
	public void accept(final T event) throws Exception {
		final InterceptorInformation preInterceptionInformation = getInterceptorInformation();
		
		final InterceptionResult preInterceptionResult = this.preInterceptor
				.map(preInterceptor -> preInterceptor.apply(preInterceptionInformation, event))
				.orElseGet(() -> InterceptionResult.success());
		
		if (!this.checkIfCorrectlyReified(event)) {
			return;
		}
		
		if (!preInterceptionResult.wasSuccessful()) {
			return;
		}
		
		final Result<?> result = this.acceptEvent(event);
		
		final InterceptionResult postInterceptionResult = this.postInterceptor
				.map(postInterceptor -> postInterceptor.apply(preInterceptionInformation, event, result))
				.orElseGet(() -> InterceptionResult.success());
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
	
	protected abstract Result<?> acceptEvent(final T event) throws Exception;
	
	protected abstract void release();
	
	protected abstract InterceptorInformation getInterceptorInformation();
	
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
	
	private boolean checkIfCorrectlyReified(final Object event) {
		if (event instanceof ReifiedEvent<?>) {
			if (this.getReifiedClasses() == null || this.getReifiedClasses().length == 0) {
				// We always accept then
				return true;
			}
			
			final ReifiedEvent<?> reifiedEvent = (ReifiedEvent<?>) event;
			return this.getReifiedClasses()[0].isAssignableFrom(reifiedEvent.getTypeToken().getRawType());
		}
		// Since it's not a reified event, check is not needed.
		return true;
	}
}
