package org.palladiosimulator.analyzer.slingshot.eventdriver;


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
		this.bus.post(new SomeOtherEvent());
	}
	
	@PreIntercept
	public InterceptionResult preIntercept(final InterceptorInformation inf, final SomeEvent event) {
		System.out.println("We intercept this SomeEvent of the method " + inf.getMethod().getName());
		return InterceptionResult.success();
	}
	
	@PreIntercept
	public InterceptionResult preIntercept(final InterceptorInformation inf, final SomeOtherEvent event) {
		System.out.println("We want to stop onAnotherTestEvent");
		if (inf.getMethod().getName().equals("onAnotherTestEvent")) {
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
		
		@OnException
		public void onException(final Exception exception) {
			System.out.println("Caught the following exception: ");
			System.out.println(exception.getClass().getSimpleName() + ": " + exception.getMessage());
		}
	}
}
