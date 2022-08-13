package org.palladiosimulator.analyzer.slingshot.eventdriver.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface OnException {
	
	public Scope[] scopes() default {};
	
	@Documented
	@Retention(RUNTIME)
	public @interface Scope {
		public String bundle() default "";
		public String javaPackage() default "";
	}
}
