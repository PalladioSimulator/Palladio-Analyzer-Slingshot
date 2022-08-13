package org.palladiosimulator.analyzer.slingshot.eventdriver.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * 
 * @author Julijan Katic
 * @see PreIntercept
 */
@Documented
@Retention(RUNTIME)
@Target({ METHOD })
public @interface PostIntercept {

}
