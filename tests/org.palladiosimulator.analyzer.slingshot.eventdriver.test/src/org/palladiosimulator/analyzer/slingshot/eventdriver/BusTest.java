package org.palladiosimulator.analyzer.slingshot.eventdriver;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.OnException;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.PreIntercept;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.internal.BusImplementation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;

@OnEvent(when = BusTest.SomeEvent.class)
@OnEvent(when = BusTest.SomeOtherEvent.class)
public class BusTest {
	
	private Bus bus;
	
	@BeforeEach
	public void createBus() {
		this.bus = new BusImplementation();
	}
	
	@Test
	public void testEventHandler() {
		this.bus.register(this);
		this.bus.register(new Other());
		this.bus.closeRegistration();
		this.bus.post(new SomeEvent());
		/* Other's handler throws: the post fails instead of dropping the handler silently. */
		final SomeOtherEvent failing = new SomeOtherEvent();
		final HandlerException e = assertThrows(HandlerException.class, () -> this.bus.post(failing));
		assertSame(failing, e.getEvent());
		assertEquals("COOL!! AN EXCEPTION OMGG!", e.getCause().getMessage());
	}

	@Test
	public void aThrowingHandlerFailsOnlyItsOwnPost() {
		final Other other = new Other();
		this.bus.register(other);
		this.bus.closeRegistration();
		assertThrows(HandlerException.class, () -> this.bus.post(new SomeOtherEvent()));
		assertEquals(1, other.exceptions);
		/* the error is reported once: the next post is not failed by it */
		this.bus.post(new SomeEvent());
	}

	@Test
	public void aThrowingHandlerStaysSubscribed() {
		final Other other = new Other();
		this.bus.register(other);
		this.bus.closeRegistration();
		assertThrows(HandlerException.class, () -> this.bus.post(new SomeOtherEvent()));
		/*
		 * the same event type again: the handler still runs and fails again (a bus that lives longer
		 * than one run, like the system bus, keeps reporting it)
		 */
		assertThrows(HandlerException.class, () -> this.bus.post(new SomeOtherEvent()));
		assertEquals(2, other.exceptions);
	}

	@PreIntercept
	public InterceptionResult preIntercept(final InterceptorInformation inf, final SomeEvent event) {
		System.out.println("We intercept this SomeEvent of the method " + inf.getName());
		return InterceptionResult.success();
	}
	
	@PreIntercept
	public InterceptionResult preIntercept(final InterceptorInformation inf, final SomeOtherEvent event) {
		System.out.println("We want to stop onAnotherTestEvent");
		if (inf.getName().equals("onAnotherTestEvent")) {
			return InterceptionResult.abort();
		} else {
			return InterceptionResult.success();
		}
	}
	
	@Subscribe
	public void onTestEvent(final SomeEvent event) {
		System.out.println("Event happened! " + event.getClass().getName());
	}
	
	@Subscribe
	public void onAnotherTestEvent(final SomeOtherEvent event) {
		System.out.println("This shouldn't happen...");
	}
	
	public static final class SomeEvent {}
	public static final class SomeOtherEvent {}
	
	@OnEvent(when = SomeOtherEvent.class)
	public static class Other {
		
		@Subscribe
		public void onSomeOtherEvent(final SomeOtherEvent event) throws Exception {
			throw new Exception("COOL!! AN EXCEPTION OMGG!");
		}
		
		int exceptions = 0;

		@OnException
		public void onException(final Exception exception) {
			this.exceptions++;
			System.out.println("Caught the following exception: ");
			System.out.println(exception.getClass().getSimpleName() + ": " + exception.getMessage());
		}
	}
}
