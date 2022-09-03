package org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
@Documented
@Repeatable(OnEvent.OnEvents.class)
public @interface OnEvent {

	public Class<?> when();
	
	public Class<?>[] then() default {};
	
	public EventCardinality cardinality() default EventCardinality.SINGLE;
	
	@Retention(RUNTIME)
	@Target(TYPE)
	@Documented
	public @interface OnEvents {
		OnEvent[] value();
	}
}
