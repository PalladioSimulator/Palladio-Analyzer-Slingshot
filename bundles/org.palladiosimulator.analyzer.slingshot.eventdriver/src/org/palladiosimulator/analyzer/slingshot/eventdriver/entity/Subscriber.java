package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * A subscriber to an event of type {@code T} that should be activated upon the event.
 * It holds a reference to a {@link EventHandler}.
 * <p>
 * It also holds further information about
 * <ul>
 *   <li>A non-negative number priority (default is 0). Subscribers are compared based on its priority.
 *   <li>An array of reified types.
 *   <li>A name that identifies this subscriber.
 *   <li>The event type itself (reified).
 *   <li>The associated contracts to this subscriber.
 *   <li>Optionally the enclosing type where the event handler is residing (i.e. if the event handler is a Java Method).
 *   <li>Optionally Pre and Post interceptors specifically for this subscriber.
 * </ul>
 * Regarding the comparison, it is only comparable to other subscribers of the same event!
 *
 * @author Julijan Katic
 *
 * @param <T> The event type
 */
public class Subscriber<T> implements Consumer<T>, Disposable, Comparable<Subscriber<T>>, InterceptorInformation {
		
	private static final Logger LOGGER = Logger.getLogger(Subscriber.class);

	private boolean disposed = false;

	private final int priority;
	private final List<Class<?>> reifiedClasses;
	private final String name;
	private final EventHandler<? super T> handler;
	private final Class<?> handlerType;
	private final Optional<Class<?>> enclosingType;
	private final Class<T> forEvent;
	private Optional<IPreInterceptor> preInterceptor;
	private Optional<IPostInterceptor> postInterceptor;
	private final List<SubscriberContract> associatedContracts;

	private Subscriber(final Builder<T> builder) {
		priority = builder.priority;
		reifiedClasses = builder.reifiedClasses;
		name = builder.name;
		handler = builder.handler;
		handlerType = builder.handlerType;
		enclosingType = builder.enclosingType;
		forEvent = builder.forEvent;
		preInterceptor = builder.preInterceptor;
		postInterceptor = builder.postInterceptor;
		associatedContracts = builder.associatedContracts;
	}

	@Override
	public void accept(final T event) throws Exception {
		final InterceptorInformation preInterceptionInformation = this;

		final InterceptionResult preInterceptionResult = preInterceptor
				.map(preInterceptor -> preInterceptor.apply(preInterceptionInformation, event))
				.orElseGet(InterceptionResult::success);

		if (!this.checkIfCorrectlyReified(event) || !preInterceptionResult.wasSuccessful()) {
			return;
		}

		final Result<?> result;
		try {
			result = handler.acceptEvent(event);
		} catch (final Throwable t) {
			throw new EventHandlerException(event, enclosingType, name, t);
		}

		// TODO: What to do with that?
		final InterceptionResult postInterceptionResult = postInterceptor
				.map(postInterceptor -> postInterceptor.apply(preInterceptionInformation, event, result))
				.orElseGet(InterceptionResult::success);
		
		LOGGER.info("Post interception result was successful: " + postInterceptionResult.wasSuccessful());
	}

	@Override
	public void dispose() {
		if (!disposed) {
			disposed = true;
		}
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public int compareTo(final Subscriber<T> other) {
		if (this.getEventType().equals(other.getEventType())) {
			return Integer.compare(priority, other.priority);
		}

		return 0;
	}

	public void addPreInterceptor(final IPreInterceptor preInterceptor) {
//		if (this.preInterceptor.isPresent()) {
//			final IPreInterceptor currentPreInterceptor = this.preInterceptor.get();
//			if (currentPreInterceptor instanceof CompositeInterceptor) {
//				final CompositeInterceptor ci = (CompositeInterceptor) currentPreInterceptor;
//				ci.add(forEvent, preInterceptor);
//			} else if (preInterceptor instanceof CompositeInterceptor) {
//				final CompositeInterceptor ci = (CompositeInterceptor) preInterceptor;
//				ci.add(forEvent, currentPreInterceptor);
//				//ci.add(forEvent, preInterceptor);
//				this.preInterceptor = Optional.of(ci);
//			} else {
//				final CompositeInterceptor ci = new CompositeInterceptor();
//				ci.add(forEvent, preInterceptor);
//				ci.add(forEvent, currentPreInterceptor);
//				this.preInterceptor = Optional.of(ci);
//			}
//		} else {
//			this.preInterceptor = Optional.of(preInterceptor);
//		}
		this.preInterceptor = Optional.of(preInterceptor);
	}

	public void addPostInterceptor(final IPostInterceptor postInterceptor) {
		this.postInterceptor = Optional.of(postInterceptor);
	}

	public int getPriority() {
		return priority;
	}

	public List<Class<?>> getReifiedClasses() {
		return reifiedClasses;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getHandlerType() {
		return handlerType;
	}

	@Override
	public Optional<Class<?>> getEnclosingType() {
		return enclosingType;
	}

	@Override
	public Class<T> getEventType() {
		return forEvent;
	}

	@Override
	public List<SubscriberContract> getAssociatedContracts() {
		return associatedContracts;
	}

	private boolean checkIfCorrectlyReified(final Object event) {
		if (event instanceof ReifiedEvent<?>) {
			if (this.getReifiedClasses() == null || this.getReifiedClasses().isEmpty()) {
				// We always accept then
				return true;
			}

			final ReifiedEvent<?> reifiedEvent = (ReifiedEvent<?>) event;
			return reifiedClasses.stream().anyMatch(type -> type.isAssignableFrom(reifiedEvent.getTypeToken().getRawType()));
		}
		// Since it's not a reified event, check is not needed.
		return true;
	}

	/**
	 * Creates builder to build {@link Subscriber}.
	 * @return created builder
	 */
	public static <T> Builder<T> builder(final Class<T> forEvent) {
		return new Builder<>(forEvent);
	}

	public static final class Builder<T> {
		private int priority = 0;
		private List<Class<?>> reifiedClasses;
		private String name;
		private EventHandler<? super T> handler;
		private Class<?> handlerType = EventHandler.class;
		private Optional<Class<?>> enclosingType = Optional.empty();
		private final Class<T> forEvent;
		private Optional<IPreInterceptor> preInterceptor = Optional.empty();
		private Optional<IPostInterceptor> postInterceptor = Optional.empty();
		private List<SubscriberContract> associatedContracts = Collections.emptyList();

		private Builder(final Class<T> forEvent) {
			this.forEvent = forEvent;
		}

		public Builder<T> priority(final int priority) {
			this.priority = priority;
			return this;
		}

		public Builder<T> reifiedClasses(final Class<?>[] reifiedClasses) {
			this.reifiedClasses = Arrays.asList(reifiedClasses);
			return this;
		}

		public Builder<T> name(final String name) {
			this.name = name;
			return this;
		}

		public Builder<T> handler(final EventHandler<? super T> handler) {
			this.handler = handler;
			return this;
		}

		public Builder<T> handlerType(final Class<?> handlerType) {
			this.handlerType = handlerType;
			return this;
		}

		public Builder<T> enclosingType(final Class<?> enclosingType) {
			this.enclosingType = Optional.ofNullable(enclosingType);
			return this;
		}

		public Builder<T> preInterceptor(final IPreInterceptor preInterceptor) {
			this.preInterceptor = Optional.ofNullable(preInterceptor);
			return this;
		}

		public Builder<T> postInterceptor(final IPostInterceptor postInterceptor) {
			this.postInterceptor = Optional.ofNullable(postInterceptor);
			return this;
		}

		public Builder<T> associatedContracts(final List<SubscriberContract> associatedContracts) {
			this.associatedContracts = associatedContracts;
			return this;
		}

		public Subscriber<T> build() {
			return new Subscriber<>(this);
		}
	}

	public Consumer<? super Throwable> onError() {
		return throwable -> {

		};
	}
}
