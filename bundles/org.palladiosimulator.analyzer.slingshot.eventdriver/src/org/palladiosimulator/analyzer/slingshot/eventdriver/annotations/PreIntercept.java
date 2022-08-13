package org.palladiosimulator.analyzer.slingshot.eventdriver.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Pre-Intercept the events of certain type. That is, before the
 * actual Subscriber methods start, methods annotated by this will
 * be called first.
 * <p>
 * These methods should NOT change the state of the system. Rather
 * these methods are meant for pre- and post-condition checking.
 * <p>
 * The methods MUST return {@link InterceptionResult}.
 * 
 * 
 * @author Julijan Katic
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ METHOD })
public @interface PreIntercept {
	
}
