package org.palladiosimulator.analyzer.slingshot.eventdriver.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Subscribe {
	
	public int priority() default 0;
	
	public Class<?>[] reified() default {};

}
