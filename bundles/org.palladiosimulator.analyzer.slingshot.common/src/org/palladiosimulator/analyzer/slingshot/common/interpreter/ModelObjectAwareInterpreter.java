package org.palladiosimulator.analyzer.slingshot.common.interpreter;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.Switch;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;

public class ModelObjectAwareInterpreter<G extends Collection<? extends DESEvent>, T extends Switch<G>> {
	
	private T delegator;
	
	public ModelObjectAwareInterpreter(final T delegator) {
		this.delegator = Objects.requireNonNull(delegator);
		this.initialize();
	}
	
	public Collection<? extends DESEvent> doSwitch(final EObject eObject) {
		final G solution = delegator.doSwitch(eObject);
		return solution;
	}
	
	private void initialize() {
		final Class<?> clazz = delegator.getClass();
		final Method[] methods = clazz.getMethods();
		
		for (final Method method : methods) {
			if (method.getName().startsWith("case")) {
				final Class<?> paramType = method.getParameterTypes()[0];
				
			}
		}
	}
}
