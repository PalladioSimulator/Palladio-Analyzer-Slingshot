package org.palladiosimulator.analyzer.slingshot.debugger.descriptor;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public record EclipseEventHandler(MethodDeclaration astNode, 
		IMethod method, 
		int lineNumber) {
}