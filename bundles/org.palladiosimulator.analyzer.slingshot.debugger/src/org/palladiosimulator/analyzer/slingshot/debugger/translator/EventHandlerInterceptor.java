package org.palladiosimulator.analyzer.slingshot.debugger.translator;

import java.util.HashSet;
import java.util.Set;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.ConcreteDebugEventHandler;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerStatus;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.OnException;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.PostIntercept;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.PreIntercept;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.EventHandlerException;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

public class EventHandlerInterceptor {

	private final Set<MethodTrack> methodTracker = new HashSet<>();

	@PostIntercept
	public InterceptionResult postTrackHandler(final InterceptorInformation interceptionInformation, final Object event,
			final Result<?> result) {
		if (event instanceof final AbstractEvent ev) {
			final DebugEventId debugEventId = new DebugEventId(ev.getId());
			
			final IDebugEventHandler handler = DebuggedHandler.fromInterceptor(interceptionInformation, debugEventId,
					HandlerStatus.SUCCESS);
			

			final HandlerId handlerId = handler.getId();
			result.getResultEvents().stream()
									.filter(AbstractEvent.class::isInstance)
									.map(AbstractEvent.class::cast)
					.forEach(resEv -> EventDebugSystem.getEventTree().addChildToParent(debugEventId,
							handlerId, new DebugEventId(resEv.getId())));

			// EventCache.getInstance().getParentTracker().put(resEv.getId(),
			// new EventTreeNode(ev.getId(), handler.getId())));
			
			EventDebugSystem.updateHandler(handler);
			methodTracker.remove(new MethodTrack(debugEventId, handlerId));
		}
		return InterceptionResult.success();
	}

	@PreIntercept
	public InterceptionResult preTrackHandler(final InterceptorInformation interceptionInformation,
			final Object event) {
		if (event instanceof final AbstractEvent ev) {
			final DebugEventId debugEventId = new DebugEventId(ev.getId());
			final IDebugEventHandler handler = DebuggedHandler.fromInterceptor(interceptionInformation, debugEventId,
					HandlerStatus.STARTED);
			final HandlerId handlerId = handler.getId();

			methodTracker.add(new MethodTrack(debugEventId, handlerId));
			EventDebugSystem.pushHandler(handler);
		}
		return InterceptionResult.success();
	}

	@OnException
	public void exceptionHappened(final EventHandlerException exception) {
		if (exception.getEvent() instanceof final AbstractEvent ev) {
			final DebugEventId debugEventId = new DebugEventId(ev.getId());
			final IDebugEventHandler handler = DebuggedHandler.fromException(exception, debugEventId);
			final HandlerId handlerId = handler.getId();

			final MethodTrack methodTrack = new MethodTrack(debugEventId, handlerId);
			if (methodTracker.contains(methodTrack)) {
				EventDebugSystem
						.updateHandler(
								new ConcreteDebugEventHandler(handlerId, debugEventId, exception.getEventHandlerName(),
								new HandlerStatus.Error("Exception thrown: " + exception.getMessage())));
				methodTracker.remove(methodTrack);
			}
		}
	}

	public record MethodTrack(DebugEventId eventId, HandlerId handlerId) {
	}
}
