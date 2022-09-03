package org.palladiosimulator.analyzer.slingshot.eventdriver.internal.contractchecker.exception;

public final class NoContractFoundException extends RuntimeException {

	private static final String EXCEPTION_FORMAT = ""
			+ "No contract was found for the method '%s' in class '%s'. "
			+ "You need to define one @OnEvent contract on class level whose "
			+ "'when' attribute should be '%s.class'";
	
	public NoContractFoundException(final String methodName,
									final String className, 
									final String eventName) {
		super(String.format(EXCEPTION_FORMAT, methodName, className, eventName));
	}
	
}
