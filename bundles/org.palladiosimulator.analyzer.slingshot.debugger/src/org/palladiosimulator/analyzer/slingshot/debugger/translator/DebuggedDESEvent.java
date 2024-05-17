package org.palladiosimulator.analyzer.slingshot.debugger.translator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.cache.EventTreeEdge;
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

	private final EventTreeEdge parent;
	private final DebugEventId id;
	private final Map<String, Object> metaInformation;

	public DebuggedDESEvent(final DESEvent actualEvent) {
		this.actualEvent = actualEvent;
		id = new DebugEventId(actualEvent.getId());
		parent = EventDebugSystem.getEventTree().getParent(id).orElse(null); // EventCache.getInstance().getParentTracker().remove(actualEvent.getId());

		if (parent != null) {
			EventDebugSystem.getEventTree().removeParent(id);
		}

		metaInformation = new HashMap<>();
		retrieveFieldValues();
	}

	private void retrieveFieldValues() {
		Class<?> tmpClass = actualEvent.getClass();

		while (tmpClass != null) {
			final Field[] fields = tmpClass.getDeclaredFields();

			for (final Field field : fields) {
				field.setAccessible(true);

				try {
					metaInformation.put(field.getName(), field.get(actualEvent).toString());
				} catch (final IllegalAccessException e) {
					// ...
				}
			}

			tmpClass = null;// tmpClass.getSuperclass();
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
		return metaInformation;
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
	public Optional<EventTreeEdge> getParentEvent() {
		return Optional.ofNullable(parent);
	}

	@Override
	public String getEventType() {
		return "simulation-event";
	}

}
