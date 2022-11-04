package org.palladiosimulator.analyzer.slingshot.common.events;

public class ModelVisited<T> extends AbstractGenericEvent<T, T> {

	public ModelVisited(Class<T> concreteClass, T entity, double delay) {
		super(concreteClass, entity, delay);
	}
	
	public ModelVisited(T entity) {
		super(entity, 0);
	}
	
}
