package org.palladiosimulator.analyzer.slingshot.mcp.runner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.mcp.config.HeadlessSimulationConfig;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationResult.MeasurementRecord;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationResult.SimulationStatus;
import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.SlingshotMeasuringValue;

/**
 * Tracks a single simulation run, collecting measurements and status information.
 */
public class SimulationSession {

    private final String sessionId;
    private final HeadlessSimulationConfig config;
    private final Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;
    private SimulationStatus status;
    private String errorMessage;
    private SimulationDriver driver;
    private final List<MeasurementRecord> measurements;
    private final AtomicLong eventCount;
    private double currentSimulationTime;

    public SimulationSession(HeadlessSimulationConfig config) {
        this(UUID.randomUUID().toString(), config);
    }

    public SimulationSession(String sessionId, HeadlessSimulationConfig config) {
        this.sessionId = sessionId;
        this.config = config;
        this.createdAt = Instant.now();
        this.status = SimulationStatus.RUNNING;
        this.measurements = Collections.synchronizedList(new ArrayList<>());
        this.eventCount = new AtomicLong(0);
        this.currentSimulationTime = 0.0;
    }

    public String getSessionId() {
        return sessionId;
    }

    public HeadlessSimulationConfig getConfig() {
        return config;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public SimulationStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public SimulationDriver getDriver() {
        return driver;
    }

    public void setDriver(SimulationDriver driver) {
        this.driver = driver;
    }

    public List<MeasurementRecord> getMeasurements() {
        return Collections.unmodifiableList(new ArrayList<>(measurements));
    }

    public int getMeasurementCount() {
        return measurements.size();
    }

    public long getEventCount() {
        return eventCount.get();
    }

    public double getCurrentSimulationTime() {
        return currentSimulationTime;
    }

    public void setCurrentSimulationTime(double time) {
        this.currentSimulationTime = time;
    }

    public void incrementEventCount() {
        this.eventCount.incrementAndGet();
    }

    /**
     * Mark the session as started.
     */
    public void markStarted() {
        this.startedAt = Instant.now();
        this.status = SimulationStatus.RUNNING;
    }

    /**
     * Mark the session as completed successfully.
     */
    public void markCompleted() {
        this.completedAt = Instant.now();
        this.status = SimulationStatus.COMPLETED;
    }

    /**
     * Mark the session as failed with an error message.
     */
    public void markFailed(String errorMessage) {
        this.completedAt = Instant.now();
        this.status = SimulationStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    /**
     * Mark the session as cancelled.
     */
    public void markCancelled() {
        this.completedAt = Instant.now();
        this.status = SimulationStatus.CANCELLED;
    }

    /**
     * Add a measurement from the simulation.
     */
    public void addMeasurement(SlingshotMeasuringValue value) {
        measurements.add(new MeasurementRecord(value));
    }

    /**
     * Check if the simulation is still running.
     */
    public boolean isRunning() {
        return status == SimulationStatus.RUNNING;
    }

    /**
     * Check if the simulation has completed (success, failure, or cancellation).
     */
    public boolean isCompleted() {
        return status == SimulationStatus.COMPLETED
                || status == SimulationStatus.FAILED
                || status == SimulationStatus.CANCELLED;
    }

    /**
     * Build a SimulationResult from this session's data.
     */
    public SimulationResult toResult() {
        return SimulationResult.builder()
                .sessionId(sessionId)
                .startTime(startedAt != null ? startedAt : createdAt)
                .endTime(completedAt != null ? completedAt : Instant.now())
                .simulatedTime(currentSimulationTime)
                .eventCount(eventCount.get())
                .status(status)
                .errorMessage(errorMessage)
                .measurements(getMeasurements())
                .build();
    }

    @Override
    public String toString() {
        return "SimulationSession{" +
                "sessionId='" + sessionId + '\'' +
                ", status=" + status +
                ", measurementCount=" + measurements.size() +
                ", eventCount=" + eventCount.get() +
                ", simulationTime=" + currentSimulationTime +
                '}';
    }
}
