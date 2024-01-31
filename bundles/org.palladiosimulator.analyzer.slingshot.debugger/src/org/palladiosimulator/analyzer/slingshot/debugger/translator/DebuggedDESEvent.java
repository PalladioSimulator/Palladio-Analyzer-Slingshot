package org.palladiosimulator.analyzer.slingshot.debugger.translator;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.cache.EventTreeNode;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.TimeInformation;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;

public final class DebuggedDESEvent implements IDebugEvent, TimeInformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient final DESEvent actualEvent;

	private final EventTreeNode parent;
	private final DebugEventId id;

	public DebuggedDESEvent(final DESEvent actualEvent) {
		this.actualEvent = actualEvent;
		id = new DebugEventId(actualEvent.getId());
		parent = EventDebugSystem.getEventTree().getParent(id).orElse(null); // EventCache.getInstance().getParentTracker().remove(actualEvent.getId());

		if (parent != null) {
			EventDebugSystem.getEventTree().removeParent(id);
		}
	}

	@Override
	public DebugEventId getId() {
		return id;
	}

	@Override
	public String getName() {
		return actualEvent.getName();
	}

	@Override
	public Object getEvent() {
		return actualEvent;
	}

	@Override
	public Map<String, Object> getMetaInformation() {
		return Collections.emptyMap();
	}

	@Override
	public TimeInformation getTimeInformation() {
		return this;
	}

	@Override
	public double getTime() {
		return actualEvent.time();
	}

	@Override
	public Optional<EventTreeNode> getParentEvent() {
		return Optional.ofNullable(parent);
	}

	@Override
	public String getEventType() {
		return "simulation-event";
	}

}
