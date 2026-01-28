package org.palladiosimulator.analyzer.slingshot.mcp.runner;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.measure.Measure;

import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.SlingshotMeasuringValue;
import org.palladiosimulator.metricspec.MetricDescription;

/**
 * Holds the results collected from a simulation run.
 * Contains all measurements organized by measuring point and metric.
 */
public class SimulationResult {

    private final String sessionId;
    private final Instant startTime;
    private final Instant endTime;
    private final Duration duration;
    private final double simulatedTime;
    private final long eventCount;
    private final SimulationStatus status;
    private final String errorMessage;
    private final List<MeasurementRecord> measurements;

    private SimulationResult(Builder builder) {
        this.sessionId = builder.sessionId;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.duration = builder.duration;
        this.simulatedTime = builder.simulatedTime;
        this.eventCount = builder.eventCount;
        this.status = builder.status;
        this.errorMessage = builder.errorMessage;
        this.measurements = Collections.unmodifiableList(new ArrayList<>(builder.measurements));
    }

    public String getSessionId() {
        return sessionId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public double getSimulatedTime() {
        return simulatedTime;
    }

    public long getEventCount() {
        return eventCount;
    }

    public SimulationStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<MeasurementRecord> getMeasurements() {
        return measurements;
    }

    public int getMeasurementCount() {
        return measurements.size();
    }

    /**
     * Get measurements filtered by metric name.
     */
    public List<MeasurementRecord> getMeasurementsByMetric(String metricName) {
        return measurements.stream()
                .filter(m -> m.getMetricName().contains(metricName))
                .collect(Collectors.toList());
    }

    /**
     * Get measurements grouped by measuring point.
     */
    public Map<String, List<MeasurementRecord>> getMeasurementsByMeasuringPoint() {
        return measurements.stream()
                .collect(Collectors.groupingBy(MeasurementRecord::getMeasuringPointName));
    }

    /**
     * Get a summary of the results suitable for display.
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Simulation Result Summary:\n");
        sb.append("  Session ID: ").append(sessionId).append("\n");
        sb.append("  Status: ").append(status).append("\n");
        sb.append("  Simulated Time: ").append(simulatedTime).append("\n");
        sb.append("  Event Count: ").append(eventCount).append("\n");
        sb.append("  Wall Clock Duration: ").append(duration.toMillis()).append(" ms\n");
        sb.append("  Measurements Collected: ").append(measurements.size()).append("\n");

        if (errorMessage != null) {
            sb.append("  Error: ").append(errorMessage).append("\n");
        }

        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Status of the simulation run.
     */
    public enum SimulationStatus {
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    /**
     * A single measurement record.
     */
    public static class MeasurementRecord {
        private final double simulationTime;
        private final String measuringPointName;
        private final String metricName;
        private final List<MeasureRecord> measures;

        public MeasurementRecord(SlingshotMeasuringValue value) {
            this.simulationTime = extractSimulationTime(value);
            this.measuringPointName = value.getMeasuringPoint() != null
                    ? value.getMeasuringPoint().getStringRepresentation()
                    : "unknown";
            this.metricName = value.getMetricDesciption() != null
                    ? value.getMetricDesciption().getName()
                    : "unknown";
            this.measures = extractMeasures(value);
        }

        private double extractSimulationTime(SlingshotMeasuringValue value) {
            try {
                List<Measure<?, ?>> measures = value.asList();
                if (!measures.isEmpty()) {
                    Measure<?, ?> timeMeasure = measures.get(0);
                    if (timeMeasure.getValue() instanceof Number) {
                        return ((Number) timeMeasure.getValue()).doubleValue();
                    }
                }
            } catch (Exception e) {
                // Ignore extraction errors
            }
            return 0.0;
        }

        private List<MeasureRecord> extractMeasures(SlingshotMeasuringValue value) {
            List<MeasureRecord> records = new ArrayList<>();
            try {
                for (Measure<?, ?> measure : value.asList()) {
                    records.add(new MeasureRecord(
                            measure.getValue(),
                            measure.getUnit().toString()
                    ));
                }
            } catch (Exception e) {
                // Ignore extraction errors
            }
            return records;
        }

        public double getSimulationTime() {
            return simulationTime;
        }

        public String getMeasuringPointName() {
            return measuringPointName;
        }

        public String getMetricName() {
            return metricName;
        }

        public List<MeasureRecord> getMeasures() {
            return measures;
        }

        @Override
        public String toString() {
            return "MeasurementRecord{" +
                    "time=" + simulationTime +
                    ", point='" + measuringPointName + '\'' +
                    ", metric='" + metricName + '\'' +
                    ", measures=" + measures +
                    '}';
        }
    }

    /**
     * A single measure value with unit.
     */
    public static class MeasureRecord {
        private final Object value;
        private final String unit;

        public MeasureRecord(Object value, String unit) {
            this.value = value;
            this.unit = unit;
        }

        public Object getValue() {
            return value;
        }

        public String getUnit() {
            return unit;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    public static class Builder {
        private String sessionId;
        private Instant startTime;
        private Instant endTime;
        private Duration duration;
        private double simulatedTime;
        private long eventCount;
        private SimulationStatus status = SimulationStatus.RUNNING;
        private String errorMessage;
        private List<MeasurementRecord> measurements = new ArrayList<>();

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(Instant endTime) {
            this.endTime = endTime;
            if (this.startTime != null) {
                this.duration = Duration.between(this.startTime, endTime);
            }
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder simulatedTime(double simulatedTime) {
            this.simulatedTime = simulatedTime;
            return this;
        }

        public Builder eventCount(long eventCount) {
            this.eventCount = eventCount;
            return this;
        }

        public Builder status(SimulationStatus status) {
            this.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder addMeasurement(MeasurementRecord measurement) {
            this.measurements.add(measurement);
            return this;
        }

        public Builder addMeasurement(SlingshotMeasuringValue value) {
            this.measurements.add(new MeasurementRecord(value));
            return this;
        }

        public Builder measurements(List<MeasurementRecord> measurements) {
            this.measurements = new ArrayList<>(measurements);
            return this;
        }

        public SimulationResult build() {
            return new SimulationResult(this);
        }
    }
}
