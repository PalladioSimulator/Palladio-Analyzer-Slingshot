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
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.AnnotatedSubscriber;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.CompositeInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.CompositePostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.CompositePreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.PostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.PreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.internal.contractchecker.EventContractChecker;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public final class BusImplementation implements Bus {
	
	private static final Logger LOGGER = Logger.getLogger(BusImplementation.class);
	
	private final Subject<Object> bus;
	
	/** Maps events (event classes) to handlers */
	private final Map<Class<?>, CompositeDisposable> observers = new HashMap<>();
	
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
	
	public <T> void register(final Subscriber<T> subscriber) {
		if (!this.registrationOpened) {
			throw new IllegalStateException("This bus has closed registration for new subscribers");
		}
		Objects.requireNonNull(subscriber, "Subscriber must not be null!");
		
		
	}

	@Override
	public void register(final Object object) {
		if (!this.registrationOpened) {
			throw new IllegalStateException("This bus does not income new objects.");
		}
		
		Objects.requireNonNull(object, "Observer to register must not be null.");
		final Class<?> observerClass = object.getClass();
		
		if (observers.putIfAbsent(observerClass, new CompositeDisposable()) != null) {
			LOGGER.warn("Observer has already been registered");
			//throw new IllegalArgumentException("Observer has already been registered.");
		}
		
		final CompositeDisposable composite = observers.get(observerClass);
		
		final Set<EventType> events = new HashSet<>();
		
		System.out.println("Register " + object.getClass().getSimpleName());
		
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
		final CompositeDisposable composite = this.observers.remove(object.getClass());
		Objects.requireNonNull(composite, "Missing observer; it was not registered before.");
		composite.dispose();
		
		final Set<Subscriber<?>> subscribers = this.subscribers.remove(observers.getClass());
		if (subscribers != null) {
			subscribers.clear();
		}
	}

	@Override
	public void post(Object event) {
		if (!this.invocationOpened) {
			throw new IllegalStateException("The bus is not currently allowing posting of events.");
		}
		System.out.println("Now post " + event.getClass().getSimpleName());
		this.bus.onNext(Objects.requireNonNull(event));
	}
	
	private CompositeDisposable getDisposable(final Class<?> event) {
		// TODO
		return null;
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
		
		
		LOGGER.debug("\tAdded subscriber method " + method.getName());
		composite.add(
				this.bus.ofType(eventClass)
						.doOnNext(ev -> System.out.println("ON NEXT " + ev.getClass().getSimpleName()))
						.subscribe(
								(Subscriber) AnnotatedSubscriber.fromJavaMethod(eventClass, returnType, method, subscribeAnnotation, compositeInterceptor, compositeInterceptor).build(),
								this::doError
						)
		);
		
	}
	
	private void doError(final Throwable error) {
		LOGGER.error("An error occurred: " + error.getClass().getSimpleName() + ":: " + error.getMessage());
		this.exceptionHandlers.keySet().stream()
				.filter(exClazz -> exClazz.isAssignableFrom(error.getClass()))
				.flatMap(exClazz -> this.exceptionHandlers.get(exClazz).stream())
				.forEach(exHandler -> exHandler.accept(error));
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
		
		if (params.length == 1) {
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
		} else {
			throw new IllegalArgumentException("");
		}
		
		this.exceptionHandlers.computeIfAbsent(params[0], eventType -> new HashSet<>())
							  .add(onException);
	}
	
	public void closeRegistration() {
		this.registrationOpened = false;
		this.invocationOpened = true;
	}
	
	public void acceptEvents(final boolean accept) {
		if (this.registrationOpened) {
			return;
		}
		this.invocationOpened = accept;
	}
	
	public static class EventType {
		private Class<?> eventClass;
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
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EventType other = (EventType) obj;
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
