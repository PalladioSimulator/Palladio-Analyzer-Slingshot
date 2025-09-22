package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.ISlingshotMeasurementSourceListener;
import org.palladiosimulator.commons.designpatterns.AbstractObservable;
import org.palladiosimulator.commons.designpatterns.IAbstractObservable;
import org.palladiosimulator.metricspec.metricentity.MetricEntity;

public class SlingshotCalculator extends MetricEntity implements IAbstractObservable<ISlingshotMeasurementSourceListener> {

	private final AbstractObservable<ISlingshotMeasurementSourceListener> observableDelegate = new AbstractObservable<>() {};
	
	@Override
	public void addObserver(ISlingshotMeasurementSourceListener observer) {
		this.observableDelegate.addObserver(observer);
	}

	@Override
	public void removeObserver(ISlingshotMeasurementSourceListener observer) {
		this.observableDelegate.removeObserver(observer);
	}

	
}
