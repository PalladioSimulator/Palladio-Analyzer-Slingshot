package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.core.api.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.ProcessingTypeRevealed;
import org.palladiosimulator.analyzer.slingshot.monitor.utils.SlingshotCalculatorWrapper;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.IObservableCalculatorRegistry;

import com.google.common.base.Supplier;

/**
 * A simulation behavior that will lazily add measurement source listeners to
 * calculators when a new processing type has been revealed
 * ({@link ProcessingTypeRevealed}).
 * <p>
 * In that way, ProcessingTypes and Calculators are decoupled and can be
 * constructed on their own, and as soon as both appropriate instances are
 * available, they will be connected.
 *
 * @author Julijan Katic
 */
@OnEvent(when = CalculatorRegistered.class, then = {})
@OnEvent(when = ProcessingTypeRevealed.class, then = {})
public class DeferredCalculatorMeasurementInitializationBehavior implements SimulationBehaviorExtension {

	/**
	 * The map that holds lazy initializers. Maps Measuring Point's String
	 * Representation to a Map of Metric-Descriptions mapping to a set of lazy
	 * initalizers.
	 */
	private final Map<String, Map<MetricDescription, Set<Supplier<IMeasurementSourceListener>>>> sourceListener = new HashMap<>();

	/** The calculator registry. */
	private final IObservableCalculatorRegistry registry;

	/**
	 * The scheduling to add events from the outside. This is needed for
	 * {@link ProcessingTypeMeasurementSourceListener}s.
	 */
	private final SimulationScheduling scheduling;

	@Inject
	public DeferredCalculatorMeasurementInitializationBehavior(
			final IObservableCalculatorRegistry registry,
			final SimulationScheduling scheduling) {
		this.registry = registry;
		this.scheduling = scheduling;
	}

	@Subscribe
	public void onCalculatorRegistered(final CalculatorRegistered register) {
		final Calculator calculator = register.getEntity();

		if (this.sourceListener.containsKey(calculator.getMeasuringPoint().getStringRepresentation())) {
			final Map<MetricDescription, Set<Supplier<IMeasurementSourceListener>>> callbacks = this.sourceListener
					.get(calculator.getMeasuringPoint().getStringRepresentation());
			final List<MetricDescription> callbacksToRemove = new LinkedList<>();
			callbacks.keySet().stream()
					.filter(metricDesc -> this.checkMetricDescriptionsAndInitializeSourceListeners(metricDesc,
							calculator, callbacks))
					.forEach(callbacksToRemove::add);
			callbacksToRemove.forEach(callbacks::remove);
			if (callbacks.isEmpty()) {
				this.sourceListener.remove(calculator.getMeasuringPoint().getStringRepresentation());
			}
		}
	}

	@Subscribe
	public void onProcessingTypeRevealed(final ProcessingTypeRevealed processingTypeRevealed) {
		final Optional<Calculator> baseCalculator = this.getBaseCalculator(
				processingTypeRevealed.getMetricDescription(), processingTypeRevealed.getMeasuringPoint());
		if (baseCalculator.isPresent()) {
			baseCalculator.get().addObserver(SlingshotCalculatorWrapper.wrap(baseCalculator.get(),
					new ProcessingTypeMeasurementSourceListener(this.scheduling,
							processingTypeRevealed.getMeasurementSourceListener())));
		} else {
			this.sourceListener.computeIfAbsent(processingTypeRevealed.getMeasuringPoint().getStringRepresentation(),
					s -> new HashMap<>())
					.computeIfAbsent(processingTypeRevealed.getMetricDescription(), d -> new HashSet<>())
					.add(() -> SlingshotCalculatorWrapper.wrap(processingTypeRevealed.getMeasuringPoint(),
							new ProcessingTypeMeasurementSourceListener(this.scheduling,
									processingTypeRevealed.getMeasurementSourceListener())));
		}

	}

	/**
	 * Checks whether the metric description is subsumed by the calculator's metric
	 * description, and if so, initializes the measurement source listeners and adds
	 * them to the calculator.
	 *
	 * @param metricDescription The metric description to check.
	 * @param calculator        The calculator, in which the measurement source
	 *                          listener is possibly added.
	 * @param callbacks         The map of the initializers.
	 * @return true iff the metric description is subsumed by the calculator's
	 *         metric description.
	 */
	private boolean checkMetricDescriptionsAndInitializeSourceListeners(
			final MetricDescription metricDescription,
			final Calculator calculator,
			final Map<MetricDescription, Set<Supplier<IMeasurementSourceListener>>> callbacks) {
		if (this.isSameOrSubsumedMetric(metricDescription, calculator.getMetricDesciption())) {
			callbacks.get(metricDescription).stream()
					.map(Supplier::get)
					.forEach(calculator::addObserver);
			return true;
		}
		return false;
	}

	/**
	 * Returns whether the first metric description is the same as or subsumes the
	 * second metric description.
	 *
	 * @return true iff the first is the same as or subsumes the second metric
	 *         description.
	 */
	private boolean isSameOrSubsumedMetric(final MetricDescription metricDescription1,
			final MetricDescription metricDescription2) {
		return metricDescription1.getId().equals(metricDescription2.getId())
				|| (metricDescription1 instanceof BaseMetricDescription
						&& MetricDescriptionUtility.isBaseMetricDescriptionSubsumedByMetricDescription(
								(BaseMetricDescription) metricDescription1, metricDescription2));
	}

	/**
	 * Returns the calculator from the registry that is associated with the metric
	 * description and the measuring point. If the metric description is a
	 * {@link BaseMetricDescription}, then the subsumed metric descriptions are also
	 * checked and the first calculator to be found is returned.
	 *
	 * @param metricDescription The metric description.
	 * @param mp                The measuring point.
	 * @return An optional calculator that matches either the exact metric
	 *         description or one of the subsumed ones and the measuring point. If
	 *         such calculator is not found, a empty optional is returned.
	 */
	private Optional<Calculator> getBaseCalculator(final MetricDescription metricDescription, final MeasuringPoint mp) {
		final Calculator baseCalculator = this.registry.getCalculatorByMeasuringPointAndMetricDescription(mp,
				metricDescription);
		if (baseCalculator == null && metricDescription instanceof BaseMetricDescription) {
			return this.registry.getCalculatorsForMeasuringPoint(mp).stream()
					.filter(calc -> MetricDescriptionUtility.isBaseMetricDescriptionSubsumedByMetricDescription(
							(BaseMetricDescription) metricDescription, calc.getMetricDesciption()))
					.findAny();
		}
		return Optional.ofNullable(baseCalculator);
	}
}
