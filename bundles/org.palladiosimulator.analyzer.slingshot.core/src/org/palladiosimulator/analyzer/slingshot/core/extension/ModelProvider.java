package org.palladiosimulator.analyzer.slingshot.core.extension;

import org.eclipse.emf.ecore.EObject;

import com.google.inject.Provider;

public interface ModelProvider<T extends EObject> extends Provider<T> {

}
