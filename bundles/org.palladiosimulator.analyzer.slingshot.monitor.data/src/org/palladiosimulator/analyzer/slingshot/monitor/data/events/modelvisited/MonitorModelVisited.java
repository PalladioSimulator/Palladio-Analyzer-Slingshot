package org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.common.events.ModelVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.MonitoringEvent;

/**
 * This event is used to describe that a certain model element in the Monitoring
 * model has been visited. The reason for this event is to enable extensibility:
 * Certain {@code ProcessingType}s can be extended further with different
 * modeling elements that are not covered in this plugin.
 *
 * <p>
 * IMPORTANT: This event uses generics. This means that in order to distinguish
 * between the different event handlers which listen to
 * {@code MonitorModelVisited}, but on a different {@code <MonitorModel>}, the
 * {@link Reified} annotation must be given, for example:
 *
 * {@code public ResultEvent<?> onCustomProcessingType(@Reified(CustomProcessingType.class) final MonitorModelVisited<CustomProcessingType> type)}.
 *
 * The reason for this is that generic types are erased during run-time, and the
 * information must be given back (in this case, by using an annotation).
 *
 * @author Julijan Katic
 *
 * @param <MonitorModel> The monitoring model that has be
 */
public class MonitorModelVisited<MonitorModel extends EObject> extends ModelVisited<MonitorModel>
		implements MonitoringEvent {

	public MonitorModelVisited(final MonitorModel entity) {
		super((Class<MonitorModel>) entity.getClass(), entity, 0.0);
	}

}
