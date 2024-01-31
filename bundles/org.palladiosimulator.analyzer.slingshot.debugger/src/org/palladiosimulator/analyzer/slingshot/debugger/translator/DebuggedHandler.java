package org.palladiosimulator.analyzer.slingshot.debugger.translator;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerStatus;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.EventHandlerException;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;

public class DebuggedHandler implements IDebugEventHandler {
	
	private final HandlerId id;
	private final String name;
	private final DebugEventId ofEvent;
	private final HandlerStatus status;
	
//	public DebuggedHandler(final InterceptorInformation interceptorInformation,
//						   final DebugEventId debugEventId,
//						   final HandlerStatus status) {
//		final String methodName = interceptorInformation.getName();
//
//
//		this(new HandlerId(name + "--of-event=" + debugEventId), enclosingName + "#" + methodName, debugEventId,
//				status);
//	}
//
//	public DebuggedHandler(final EventHandlerException exception) {
//
//	}

	public DebuggedHandler(final HandlerId id, final String name, final DebugEventId ofEvent,
			final HandlerStatus status) {
		this.id = id;
		this.name = name;
		this.ofEvent = ofEvent;
		this.status = status;
	}


	@Override
	public HandlerId getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public DebugEventId ofEvent() {
		// TODO Auto-generated method stub
		return ofEvent;
	}

	@Override
	public HandlerStatus getStatus() {
		// TODO Auto-generated method stub
		return status;
	}

	public static DebuggedHandler fromInterceptor(final InterceptorInformation interceptorInformation,
			final DebugEventId debugEventId, final HandlerStatus status) {
		final String name = getCorrectName(interceptorInformation);
		final HandlerId handlerId = new HandlerId(name + "--of-event=" + debugEventId);
		return new DebuggedHandler(handlerId, name, debugEventId, status);
	}

	public static DebuggedHandler fromException(final EventHandlerException exception, final DebugEventId eventId) {
		final String name = getCorrectName(exception);
		final HandlerId handlerId = new HandlerId(name + "--of-event=" + eventId);
		return new DebuggedHandler(handlerId, name, eventId, new HandlerStatus.Error(exception.getMessage()));
	}

	private static String getCorrectName(final InterceptorInformation interceptorInformation) {
		final String enclosingName;
		if (interceptorInformation.getEnclosingType().isPresent()) {
			enclosingName = interceptorInformation.getEnclosingType().get().getName();
		} else {
			enclosingName = "Anonymous";
		}
		return enclosingName + "#" + interceptorInformation.getName();
	}

	private static String getCorrectName(final EventHandlerException exception) {
		final String enclosingName;
		if (exception.getEnclosingType().isPresent()) {
			enclosingName = exception.getEnclosingType().get().getName();
		} else {
			enclosingName = "Anonymous";
		}
		return enclosingName + "#" + exception.getEventHandlerName();
	}
}
