package org.palladiosimulator.analyzer.slingshot.eventdriver.entity;

import com.google.common.reflect.TypeToken;

public interface ReifiedEvent<T> {
	
	public TypeToken<T> getTypeToken();
	
}
