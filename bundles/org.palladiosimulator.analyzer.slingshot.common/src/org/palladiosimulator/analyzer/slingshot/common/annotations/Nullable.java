package org.palladiosimulator.analyzer.slingshot.common.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A Guice compatible annotation so that an injector allows {@code nulls}
 * into constructors.
 * 
 * @author Julijan Katic
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER, TYPE_USE })
public @interface Nullable {

}
