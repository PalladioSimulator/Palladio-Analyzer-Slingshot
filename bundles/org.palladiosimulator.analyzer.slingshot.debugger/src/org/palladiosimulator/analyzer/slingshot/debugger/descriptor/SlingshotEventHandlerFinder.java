package org.palladiosimulator.analyzer.slingshot.debugger.descriptor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.swt.widgets.Display;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.common.JDTHelper;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.handler.EventHandlerChecker;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.handler.EventHandlerFinder;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;

public class SlingshotEventHandlerFinder
		implements EventHandlerFinder<IType, EclipseEventHandler>, EventHandlerChecker {

	@Override
	public void retrieveMethods(final IType eventType, final Consumer<EclipseEventHandler> doOnFound,
			final Runnable onCompletion) {
		final Job job = new FindHandlerJob(eventType, doOnFound, onCompletion);
		job.setUser(true);
		job.schedule();
	}
	
	@Override
	public boolean isEventHandler(final Object object) {
		if (object instanceof final IMethod method) {

			try {
				return Arrays.stream(method.getAnnotations())
							.anyMatch(annotation -> annotation.getElementName().equals(Subscribe.class.getSimpleName())
									|| annotation.getElementName().equals(Subscribe.class.getName()));
			} catch (final JavaModelException e) {
				e.printStackTrace();
			}

		}

		return false;
	}

	private static final class FindHandlerJob extends Job {

		private final IType eventType;
		private final Consumer<EclipseEventHandler> doOnFound;
		private final Runnable onCompletion;

		public FindHandlerJob(final IType eventType, final Consumer<EclipseEventHandler> doOnFound,
				final Runnable onCompletion) {
			super(String.format("Find all event methods for the event \"%s\"", eventType.getElementName()));
			this.eventType = eventType;
			this.doOnFound = doOnFound;
			this.onCompletion = onCompletion;
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final ASTEventHandlerFinder finder = new ASTEventHandlerFinder(eventType, doOnFound);
			final SubMonitor subMonitor = SubMonitor.convert(monitor);
			finder.monitor = subMonitor;

			JDTHelper.visitEveryCU(finder, () -> "Cannot find all cu.");
			Display.getDefault().asyncExec(onCompletion);

			return Status.OK_STATUS;
		}

	}

	private static final class ASTEventHandlerFinder extends JDTHelper.ASTVisitorWithCU {

		private final IType type;
		private final Consumer<EclipseEventHandler> doOnFound;

		private long numberOperationsChecked = 0;
		private IProgressMonitor monitor;

		public ASTEventHandlerFinder(final IType type, final Consumer<EclipseEventHandler> doOnFound) {
			this.type = type;
			this.doOnFound = doOnFound;
		}

		@Override
		public boolean visit(final MethodDeclaration node) {
			numberOperationsChecked++;

			monitor.setTaskName(String.format("Check for method %s", node.getName().getIdentifier()));
			monitor.worked(1);
			if (isSubscribedMethodAnnotated(node) && isSubscribedMethodRightType(node)) {
				final Block block = node.getBody();
				if (block == null) {
					return false;
				}

				final List<Statement> statements = block.statements();
				if (statements.isEmpty()) {
					return false;
				}

				final IMethod method = (IMethod) node.resolveBinding().getJavaElement();
				final int lineNumber = cu.getLineNumber(statements.get(0).getStartPosition());
				final EclipseEventHandler handler = new EclipseEventHandler(node, method, lineNumber);
				doOnFound.accept(handler);
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		private boolean isSubscribedMethodAnnotated(final MethodDeclaration node) {
			if (node.parameters().size() != 1) {
				return false;
			}
			final List<IExtendedModifier> modifiers = node.modifiers();
			return modifiers.stream()
					.filter(Annotation.class::isInstance)
					.map(Annotation.class::cast)
					.anyMatch(annotation -> annotation.getTypeName().getFullyQualifiedName()
							.equals(Subscribe.class.getSimpleName()));
		}

		private boolean isSubscribedMethodRightType(final MethodDeclaration node) {
			final SingleVariableDeclaration param = (SingleVariableDeclaration) node.parameters().get(0);
			final IType paramType = (IType) param.getType().resolveBinding().getJavaElement();
			return paramType.getFullyQualifiedName().equals(type.getFullyQualifiedName());
		}
	}

}
