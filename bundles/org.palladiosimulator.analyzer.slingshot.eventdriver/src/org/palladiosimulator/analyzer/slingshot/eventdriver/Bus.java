package org.palladiosimulator.analyzer.slingshot.eventdriver;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.AbstractSubscriber;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPostInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.IPreInterceptor;
import org.palladiosimulator.analyzer.slingshot.eventdriver.internal.BusImplementation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

/**
 * A general Bus interface that can handle events of any type, and allows adding subscribers
 * to events.
 * 
 * The subscribers themselves can be arbitrarily defined, and need to inherit from {@link AbstractSubscriber}.
 * The most used subscription type is the {@link AnnotatedSubscriber}, but it is also possible
 * to and subscribers in a functional style using {@link LambdaBasedSubscriber}.
 * 
 * Each bus has an identifier for logging purposes. It can either be generated or provided by the user.
 * 
 * @author Julijan Katic
 * @version 2023-01-24
 */
public interface Bus {

	/**
	 * The unique identifer of this bus. This can be generated or provided by the user.
	 * @return The unique identifier.
	 */
	public String getIdentifier();
	
	/**
	 * The most generic registration of an event handler, where an own type of subscriber can be added.
	 * 
	 * @param <T> The type of the event.
	 * @param forEvent
	 * @param subscriber
	 */
	public <T> void registerSubscriber(final Class<T> forEvent, final AbstractSubscriber<T> subscriber);
	
	/**
	 * Registers a new object as an {@link AnnotatedSubscriber} and adds the possibility to register
	 * each of its method as a subscriber, interceptor or error-handler. This is the most common
	 * form of event subscription.
	 * 
	 * To add a functional subscriber, see {@link #registerHandler}.
	 * 
	 * @param object The object whose methods should be registered that are appropriately annotated.
	 */
	public void register(final Object object);
	
	/**
	 * Registers a new event handler directly to the system as a {@link LambdaBasedSubscriber}.
	 * 
	 * @param <T> The type of the event to listen to.
	 * @param forEvent The event type as a {@code Class}.
	 * @param handler The function to register as the event's subscriber.
	 */
	public <T> void registerHandler(final Class<T> forEvent, final Function<T, ? extends Result<?>> handler);
	
	/**
	 * Unregisters an object containing subscribers. Every subscriber inside will be ignored from now on.
	 * 
	 * @param object The object to unregister.
	 */
	public void unregister(final Object object);
	
	/**
	 * Adds a pre interceptor.
	 * @param preInterceptor
	 */
	public void addPreInterceptor(final Class<?> forEvent, final IPreInterceptor preInterceptor);
	
	public void addPostInterceptor(final Class<?> forEvent, final IPostInterceptor postInterceptor);
	
	public <T extends Throwable> void addExceptionHandler(final Class<T> onException, final Consumer<T> exceptionHandler);
	
	/**
	 * Posts an event and calls each handler that are subscribed to, if the interceptors allow that.
	 * 
	 * @param event The event to post.
	 */
	public void post(final Object event);

	public void closeRegistration();
	
	public void acceptEvents(final boolean accept);
	
	public void clear();
	
	public static Bus instance() {
		return new BusImplementation();
	}
	
	public static Bus instance(final String name) {
		return new BusImplementation(name);
	}
}
