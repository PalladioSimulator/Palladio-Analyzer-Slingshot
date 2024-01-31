package org.palladiosimulator.analyzer.slingshot.debugger.descriptor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.eclipse.model.EclipseEventBreakpoint;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.BreakpointEventListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.BreakpointEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerFound;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.EventBreakpoint;

public class EclipseEventDebugObserver implements BreakpointEventListener {
	
	public static final String IS_EVENT_BP = "isEventBp";
	public static final String EVENT_TYPE = "eventType";
	
	private final Map<EventBreakpoint, List<IJavaBreakpoint>> breakpointMap;

	public EclipseEventDebugObserver() {
		breakpointMap = new HashMap<>();
	}

	private void eventBreakpointAdded(final EventBreakpoint eventBreakpoint) {
		breakpointMap.computeIfAbsent(eventBreakpoint, eb -> new LinkedList<>());
		if (eventBreakpoint instanceof final EclipseEventBreakpoint eclipseEventBreakpoint) {
			EventDebugSystem.getEventHandlerFinderHolder().findEventHandlers(eclipseEventBreakpoint.getType(),
					IType.class, EclipseEventHandler.class,
					method -> {
				addBreakpointToMethod(eclipseEventBreakpoint, method);
			}, null);
		}
	}

	private void eventBreakpointRemoved(final EventBreakpoint eventBreakpoint) {
		final List<IJavaBreakpoint> points = breakpointMap.remove(eventBreakpoint);
		if (points == null) {
			return;
		}
		
		points.forEach(point -> {
			try {
				DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(point, true);
			} catch (final CoreException e) {
				e.printStackTrace();
			}
		});
	}
	
	@Override
	public void onEvent(final BreakpointEvent ev) {
		switch (ev.getSpecificEventType()) {
		case ADDED:
			this.eventBreakpointAdded(ev.getEventBreakpoint());
			break;
		case REMOVED:
			this.eventBreakpointRemoved(ev.getEventBreakpoint());
			break;
		}
	}
	
	private void addBreakpointToMethod(final EventBreakpoint point, final EclipseEventHandler method) {
		try {
			final IJavaLineBreakpoint lineBreakpoint = JDIDebugModel.createLineBreakpoint(method.method().getResource(), 
					method.method().getDeclaringType().getFullyQualifiedName(), 
					method.lineNumber(), -1, -1, 0, true, 
					createAttributes(method.method().getDeclaringType()));
			breakpointMap.get(point).add(lineBreakpoint);
		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, Object> createAttributes(final IType type) {
		final Map<String, Object> result = new HashMap<>();
		result.put(IS_EVENT_BP, Boolean.TRUE);
		result.put(EVENT_TYPE, type.getFullyQualifiedName());
		return result;
	}

	private static final class EventBreakpointToMethodCalled
			extends EventHandlerFound<EclipseEventHandler> {

		public EventBreakpointToMethodCalled(final EclipseEventHandler method) {
			super(method);
			// TODO Auto-generated constructor stub
		}

	}
}
