package org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface ListenableBy {

	public String[] bundles() default { ALL };
	public String[] packages() default { ALL };
	public Class<?>[] classes() default {}; // empty array means "all"
	
	public static final String ALL = "*";
	
}
