package org.palladiosimulator.analyzer.slingshot.eventdriver;


import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public class BusTestDynamicHandlers {

	private final Bus bus = Bus.instance("test");
	private boolean hasWorked;
	
	@BeforeEach
	public void init() {
		this.hasWorked = false;
	}
	
	@Test
	public void testCheckIfHandlerExist() {
		bus.registerHandler(SomeEvent.class, event -> {
			hasWorked = true;
			return Result.empty();
		});
		bus.post(new SomeEvent());
		assertTrue(hasWorked);
	}
	
	public static final class SomeEvent {}
}
