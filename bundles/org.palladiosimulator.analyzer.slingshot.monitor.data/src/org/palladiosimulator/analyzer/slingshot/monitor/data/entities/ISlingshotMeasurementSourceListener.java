package org.palladiosimulator.analyzer.slingshot.monitor.data.entities;

public interface ISlingshotMeasurementSourceListener {
	
	public void newSlingshotMeasurementAvailable(final SlingshotMeasuringValue newMeasuringValue);

	public void preUnregister();
	
}
