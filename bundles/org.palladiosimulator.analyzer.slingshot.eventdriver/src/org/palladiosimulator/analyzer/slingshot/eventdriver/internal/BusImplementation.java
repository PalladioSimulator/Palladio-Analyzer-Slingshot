package org.palladiosimulator.analyzer.slingshot.eventdriver.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.OnException;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.PostIntercept;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.PreIntercept;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.AnnotatedSubscriber;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.CompositeInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.PostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.PreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.internal.contractchecker.EventContractChecker;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

/**
 *
 *
 * @author Julijan Katic
 *
 */
public final class BusImplementation implements Bus {

	private static final Logger LOGGER = Logger.getLogger(BusImplementation.class);

	private final Subject<Object> bus;

	/** Maps IDs to handlers */
	private final Map<String, CompositeDisposable> observers = new HashMap<>();

	/** The global interceptor */
	private final CompositeInterceptor compositeInterceptor = new CompositeInterceptor();

	/** Maps exception classes to set of exception handlers */
	private final Map<Class<?>, Set<Consumer<? super Throwable>>> exceptionHandlers = new HashMap<>();


	private final Map<Class<?>, Set<Subscriber<?>>> subscribers = new HashMap<>();


	private boolean registrationOpened = true;
	private boolean invocationOpened = true;

	private final String identifier;


	public BusImplementation(final String identifier) {
		this.identifier = Objects.requireNonNull(identifier);
		this.bus = PublishSubject.create();
		this.init();
	}

	private void init() {
		this.register(new EventContractChecker());
	}

	public BusImplementation() {
		this("default");
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public <T> void register(final Subscriber<T> subscriber) {
		if (!this.registrationOpened) {
			throw new IllegalStateException("This bus has closed registration for new subscribers");
		}
		Objects.requireNonNull(subscriber, "Subscriber must not be null!");

		subscriber.addPreInterceptor(compositeInterceptor);
		subscriber.addPostInterceptor(compositeInterceptor);

		observers.computeIfAbsent(subscriber.getEnclosingType()
											.map(Class::getName)
											.orElseGet(subscriber::getName), id -> new CompositeDisposable())
				 .add(this.bus.ofType(subscriber.getEventType())
						 	  .doOnNext(this::doOnNext)
						 	  .subscribe(subscriber, this::doError));
	}

	@Override
	public void register(final Object object) {
		if (!this.registrationOpened) {
			throw new IllegalStateException("This bus does not income new objects.");
		}

		Objects.requireNonNull(object, "Observer to register must not be null.");
		final Class<?> observerClass = object.getClass();


		final CompositeDisposable composite = observers.computeIfAbsent(observerClass.getName(), name -> new CompositeDisposable());

		final Set<EventType> events = new HashSet<>();

		LOGGER.info("Register " + object.getClass().getSimpleName());

		for (final Method method : observerClass.getDeclaredMethods()) {
			if (method.isBridge() || method.isSynthetic()) {
				continue;
			}

			this.searchForSubscribers(composite, events, method, object);
			this.searchExceptionHandlers(method, object);
			this.searchPreInterceptors(method, object);
			this.searchPostInterceptors(method, object);
		}
	}

	@Override
	public void unregister(final Object object) {
		Objects.requireNonNull(object, "Observer to unregister must not be null.");
		final CompositeDisposable composite;
		if (object instanceof String) {
			composite = this.observers.remove(object);
		} else {
			composite = this.observers.remove(object.getClass().getName());
		}
		Objects.requireNonNull(composite, "Missing observer; it was not registered before.");
		composite.dispose();
		final Set<Subscriber<?>> subscribers = this.subscribers.remove(observers.getClass());
		if (subscribers != null) {
			subscribers.clear();
		}
	}

	@Override
	public void post(final Object event) {
		if (!this.invocationOpened) {
			throw new IllegalStateException("The bus is not currently allowing posting of events.");
		}
		LOGGER.debug("Now post " + event.getClass().getSimpleName());
		this.bus.onNext(Objects.requireNonNull(event));
	}

	private void searchForSubscribers(final CompositeDisposable composite,
			final Set<EventType> events, final Method method, final Object object) {
		if (!method.isAnnotationPresent(Subscribe.class)) {
			return;
		}
		final int modifiers = method.getModifiers();
		final Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);

		if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
			throw new IllegalArgumentException("Method " + method.getName() + " has @Subscribe annotation, but is static or is not public.");
		}

		final Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length != 1) {
			throw new IllegalArgumentException("Method " + method.getName() + " has @Subscribe annotation, but has either 0 or more than one parameters.");
		}
		final Class<?> eventClass = parameterTypes[0];
		final EventType eventType = new EventType(eventClass, subscribeAnnotation.reified());

		if (!events.add(eventType)) {
			throw new IllegalArgumentException("Subscriber for " + eventType.toString() + " has already been registered.");
		}

		EventContractChecker.checkEventContract(method, object, eventClass);

		final Class<?> returnType = method.getReturnType();
		if (!returnType.equals(void.class) && !returnType.equals(Void.class) && !returnType.equals(Result.class)) {
			throw new IllegalArgumentException("Observables must return either void (primitive), Void (object) or Result, but this method returns " + returnType.getSimpleName());
		}


		this.register(AnnotatedSubscriber.fromJavaMethod(eventClass, object, method, subscribeAnnotation, compositeInterceptor, compositeInterceptor).build());
	}

	private void doError(final Throwable error) {
		LOGGER.error("An error occurred: " + error.getClass().getSimpleName() + ":: " + error.getMessage(), error);
		this.exceptionHandlers.keySet().stream()
				.filter(exClazz -> exClazz.isAssignableFrom(error.getClass()))
				.flatMap(exClazz -> this.exceptionHandlers.get(exClazz).stream())
				.forEach(exHandler -> exHandler.accept(error));
	}

	/**
	 * Helper method for debuging purposes. Every time an event happens (no matter what),
	 * even if an error happened, this method will be called and debugged.
	 *
	 * @param event The event that happened
	 */
	private void doOnNext(final Object event) {
		LOGGER.debug("About to call the subscribers for the following events: " + event.getClass().getSimpleName());
	}

	private void searchPreInterceptors(final Method method, final Object object) {
		if (!method.isAnnotationPresent(PreIntercept.class)) {
			return;
		}

		if (!Modifier.isPublic(method.getModifiers())) {
			throw new IllegalArgumentException("Method " + method.getName() + " for pre-interception is not public.");
		}

		final PreInterceptor preInterceptor = new PreInterceptor(method, object);

		this.compositeInterceptor.add(preInterceptor.forEvent(), preInterceptor);
	}

	private void searchPostInterceptors(final Method method, final Object object) {
		if (!method.isAnnotationPresent(PostIntercept.class)) {
			return;
		}
		if (!Modifier.isPublic(method.getModifiers())) {
			throw new IllegalArgumentException("Method " + method.getName() + " for post-interception is not public.");
		}
		final PostInterceptor postInterceptor = new PostInterceptor(method, object);
		this.compositeInterceptor.add(postInterceptor.forEvent(), postInterceptor);
	}

	private void searchExceptionHandlers(final Method method, final Object target) {
		if (!method.isAnnotationPresent(OnException.class)) {
			return;
		}
		final Class<?>[] params = method.getParameterTypes();

		final Consumer<? super Throwable> onException;

		if (params.length != 1) {
			throw new IllegalArgumentException("");
		}
		if (!Throwable.class.isAssignableFrom(params[0])) {
			throw new IllegalArgumentException("First parameter must be throwable type");
		}

		onException = exception -> {
			try {
				method.invoke(target, exception);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO:
			}
		};


		this.exceptionHandlers.computeIfAbsent(params[0], eventType -> new HashSet<>())
							  .add(onException);
	}

	@Override
	public void closeRegistration() {
		this.registrationOpened = false;
		this.invocationOpened = true;
	}

	@Override
	public void acceptEvents(final boolean accept) {
		if (this.registrationOpened) {
			return;
		}
		this.invocationOpened = accept;
	}

	public static class EventType {
		private final Class<?> eventClass;
		private Class<?>[] reification;

		public EventType(final Class<?> eventClass, final Class<?>[] reification) {
			this.eventClass = eventClass;
			this.reification = reification;
			if (this.reification == null) {
				this.reification = new Class<?>[] {};
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(reification);
			result = prime * result + Objects.hash(eventClass);
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final EventType other = (EventType) obj;
			return Objects.equals(eventClass, other.eventClass) && Arrays.equals(reification, other.reification);
		}

		@Override
		public String toString() {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("EventType[event = <");
			stringBuilder.append(eventClass.getName());
			stringBuilder.append(">, reified = {");

			for (final Class<?> clazz : this.reification) {
				stringBuilder.append("<");
				stringBuilder.append(clazz.getName());
				stringBuilder.append(">");
			}

			stringBuilder.append("}]");
			return stringBuilder.toString();
		}
	}
}
