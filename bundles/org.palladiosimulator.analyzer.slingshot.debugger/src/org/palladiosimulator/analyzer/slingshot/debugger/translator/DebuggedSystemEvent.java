package org.palladiosimulator.analyzer.slingshot.debugger.translator;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.cache.EventTreeNode;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.TimeInformation;
import org.palladiosimulator.analyzer.slingshot.common.events.SystemEvent;

public class DebuggedSystemEvent implements IDebugEvent, TimeInformation {

	public static long instanceNumber;

	private final SystemEvent systemEvent;
	private final long time;
	private final DebugEventId id;

	public DebuggedSystemEvent(final SystemEvent systemEvent) {
		this.systemEvent = systemEvent;
		time = instanceNumber;
		instanceNumber++;
		id = new DebugEventId(systemEvent.getId());
	}

	@Override
	public DebugEventId getId() {
		return id;
	}

	@Override
	public String getName() {
		return systemEvent.getName();
	}

	@Override
	public Object getEvent() {
		return systemEvent;
	}

	@Override
	public Optional<EventTreeNode> getParentEvent() {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Map<String, Object> getMetaInformation() {
		return Collections.emptyMap();
	}

	@Override
	public TimeInformation getTimeInformation() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public double getTime() {
		// TODO Auto-generated method stub
		return time;
	}

	@Override
	public String getEventType() {
		// TODO Auto-generated method stub
		return "system-event";
	}

}
