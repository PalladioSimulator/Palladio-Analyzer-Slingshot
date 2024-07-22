package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
		this.priority = builder.priority;
		this.reifiedClasses = builder.reifiedClasses;
		this.name = builder.name;
		this.handler = builder.handler;
		this.handlerType = builder.handlerType;
		this.enclosingType = builder.enclosingType;
		this.forEvent = builder.forEvent;
		this.preInterceptor = builder.preInterceptor;
		this.postInterceptor = builder.postInterceptor;
		this.associatedContracts = builder.associatedContracts;
	}


	@Override
	public void accept(final T event) throws Throwable {
		final InterceptorInformation preInterceptionInformation = this;

		final InterceptionResult preInterceptionResult = this.preInterceptor
				.map(preInterceptor -> preInterceptor.apply(preInterceptionInformation, event))
				.orElseGet(InterceptionResult::success);

		if (!this.checkIfCorrectlyReified(event) || !preInterceptionResult.wasSuccessful()) {
			return;
		}

		final Result<?> result = this.handler.acceptEvent(event);


		// TODO: What to do with that?
		final InterceptionResult postInterceptionResult = this.postInterceptor
				.map(postInterceptor -> postInterceptor.apply(preInterceptionInformation, event, result))
				.orElseGet(InterceptionResult::success);

		LOGGER.info("Post interception result was successful: " + postInterceptionResult.wasSuccessful());
	}

	@Override
	public void dispose() {
		if (!disposed) {
			this.disposed = true;
		}
	}

	@Override
	public boolean isDisposed() {
		return this.disposed;
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
		return this.priority;
	}

	public List<Class<?>> getReifiedClasses() {
		return this.reifiedClasses;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getHandlerType() {
		return this.handlerType;
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
			return this.reifiedClasses.stream().anyMatch(type -> type.isAssignableFrom(reifiedEvent.getTypeToken().getRawType()));
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

	@Override
	public int hashCode() {
		return Objects.hash(associatedContracts, disposed, enclosingType, forEvent, handler, handlerType, name,
				postInterceptor, preInterceptor, priority, reifiedClasses);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Subscriber other = (Subscriber) obj;
		return Objects.equals(associatedContracts, other.associatedContracts) && disposed == other.disposed
				&& Objects.equals(enclosingType, other.enclosingType) && Objects.equals(forEvent, other.forEvent)
				&& Objects.equals(handler, other.handler) && Objects.equals(handlerType, other.handlerType)
				&& Objects.equals(name, other.name) && Objects.equals(postInterceptor, other.postInterceptor)
				&& Objects.equals(preInterceptor, other.preInterceptor) && priority == other.priority
				&& Objects.equals(reifiedClasses, other.reifiedClasses);
	}
}
