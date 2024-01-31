package org.palladiosimulator.analyzer.slingshot.eventdriver;

import java.util.function.Consumer;

import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;
import org.palladiosimulator.analyzer.slingshot.eventdriver.internal.BusImplementation;

/**
 * A general Bus interface that can handle events of any type, and allows adding subscribers
 * to events.
 * 
 * The actual subscriber is always of type {@link Subscriber}. If, however, an Object of any other
 * type is passed instead, the respective methods of that class will be looked up and searched for
 * subscribers. Thus, one can either add method-based subscribers from the annotation, or define
 * a subscriber at runtime directly.
 * 
 * Each bus has an identifier for logging purposes. It can either be generated or provided by the user.
 * 
 * @author Julijan Katic
 */
public interface Bus {

	/**
	 * The unique identifer of this bus. This can be generated or provided by the user.
	 * @return The unique identifier.
	 */
	public String getIdentifier();
	
	/**
	 * Registers a subscriber for the event of type {@code T}. The subscriber
	 * holds a name that is used to identify it and later be unsubscribed again using
	 * {@link #unregister(Object)}.
	 * 
	 * @param <T> The event type
	 * @param subscriber The subscriber holding the event handler.
	 */
	public <T> void register(final Subscriber<T> subscriber);
	
	/**
	 * Registers an object that contains method annotated by {@link Subscribe} annotations.
	 * This object must also be annotated with {@link OnEvent}s to be correctly registered.
	 * 
	 * The id of these subscribers will be the full canonical name of the method (including
	 * the name of the class).
	 * 
	 * @param object The object containing subscriber methods.
	 */
	public void register(final Object object);
	
	/**
	 * Unregisters either an object containing subscriber methods, or a certain subscriber
	 * through the name. That is, the {@code object} parameter can be an arbitrary object
	 * of subscriber methods, or of type {@link String} that identifies a certain subscriber.
	 * 
	 * Afterwards, those affected subscribers will be ignored upon the respective events.
	 * 
	 * @param object The object holding the subscriber methods OR the id of the subscriber as a String.
	 */
	public void unregister(final Object object);
	
	/**
	 * Posts an event and calls each handler that are subscribed to, if the interceptors allow that.
	 * 
	 * @param event The event to post.
	 */
	public void post(final Object event);
	
	/**
	 * Closes the registration of subscribers. After this method is called, it is not possible
	 * to register new objects.
	 */
	public void closeRegistration();
	
	/**
	 * Sets whether events can be posted to this bus or not.
	 * 
	 * @param accept A flag saying whether events can be posted here. If {@code true}, then events can
	 * 				 be posted (default behavior). If {@code false}, events are not allowed to be posted
	 * 				 here until it is opened again.
	 */
	public void acceptEvents(final boolean accept);
	
	/**
	 * This adds a listener every time an event has been posted.
	 * Unlike normal event handlers, these functions are called 
	 * just before the handlers. Furthermore, the listeners are
	 * called always, regardless of the event type.
	 * 
	 * @param eventListener A listener that should be executed 
	 * 						before the handlers.
	 */
	public void addEventListener(final Consumer<Object> eventListener);
	
	/**
	 * Returns a new instance of this bus with a default identifier.
	 * @return A new instance with default identifier.
	 */
	public static Bus instance() {
		return new BusImplementation();
	}
	
	/**
	 * Returns a new instance of this bus with the identifier given by
	 * {@code name}.
	 * 
	 * @param name The identifier of the bus that should be created.
	 * @return A new bus with the given identifier.
	 */
	public static Bus instance(final String name) {
		return new BusImplementation(name);
	}

}
