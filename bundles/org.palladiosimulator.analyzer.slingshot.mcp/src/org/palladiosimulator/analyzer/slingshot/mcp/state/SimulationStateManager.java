package org.palladiosimulator.analyzer.slingshot.mcp.state;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.mcp.config.HeadlessSimulationConfig;
import org.palladiosimulator.analyzer.slingshot.mcp.headless.StandaloneModelLoader.ModelLoadingException;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationResult;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationSession;
import org.palladiosimulator.analyzer.slingshot.mcp.runner.SlingshotRunner;

/**
 * Manages simulation sessions and their lifecycle.
 * Provides methods to create, run, and query sessions.
 */
public class SimulationStateManager {

    private static final Logger LOGGER = Logger.getLogger(SimulationStateManager.class);

    private final SessionStore sessionStore;
    private final SlingshotRunner runner;
    private final ExecutorService executor;

    public SimulationStateManager() {
        this.sessionStore = new SessionStore();
        this.runner = new SlingshotRunner();
        this.executor = Executors.newCachedThreadPool();
    }

    public SimulationStateManager(SlingshotRunner runner) {
        this.sessionStore = new SessionStore();
        this.runner = runner;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * Create a new simulation session with a generated ID.
     *
     * @param config The simulation configuration
     * @return The created session
     */
    public SimulationSession createSession(HeadlessSimulationConfig config) {
        String sessionId = UUID.randomUUID().toString();
        return createSession(sessionId, config);
    }

    /**
     * Create a new simulation session with a specific ID.
     *
     * @param sessionId The session ID
     * @param config The simulation configuration
     * @return The created session
     */
    public SimulationSession createSession(String sessionId, HeadlessSimulationConfig config) {
        if (sessionStore.contains(sessionId)) {
            throw new IllegalArgumentException("Session already exists: " + sessionId);
        }

        SimulationSession session = new SimulationSession(sessionId, config);
        sessionStore.put(session);
        LOGGER.info("Created session: " + sessionId);
        return session;
    }

    /**
     * Run a simulation synchronously.
     *
     * @param sessionId The session ID
     * @return The simulation result
     * @throws ModelLoadingException If models cannot be loaded
     * @throws IllegalArgumentException If session not found
     */
    public SimulationResult runSimulation(String sessionId) throws ModelLoadingException {
        SimulationSession session = getSessionOrThrow(sessionId);
        LOGGER.info("Running simulation for session: " + sessionId);

        SimulationResult result = runner.runSimulation(session.getConfig());

        // Update session with results
        if (result.getStatus() == SimulationResult.SimulationStatus.COMPLETED) {
            session.markCompleted();
        } else if (result.getStatus() == SimulationResult.SimulationStatus.FAILED) {
            session.markFailed(result.getErrorMessage());
        }

        return result;
    }

    /**
     * Run a simulation asynchronously.
     *
     * @param sessionId The session ID
     * @return A future that completes with the simulation result
     */
    public CompletableFuture<SimulationResult> runSimulationAsync(String sessionId) {
        SimulationSession session = getSessionOrThrow(sessionId);
        LOGGER.info("Starting async simulation for session: " + sessionId);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return runner.runSimulation(session.getConfig());
            } catch (ModelLoadingException e) {
                session.markFailed("Model loading error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }, executor);
    }

    /**
     * Get a session by ID.
     *
     * @param sessionId The session ID
     * @return Optional containing the session if found
     */
    public Optional<SimulationSession> getSession(String sessionId) {
        return sessionStore.get(sessionId);
    }

    /**
     * Get a session or throw if not found.
     *
     * @param sessionId The session ID
     * @return The session
     * @throws IllegalArgumentException If session not found
     */
    public SimulationSession getSessionOrThrow(String sessionId) {
        return sessionStore.get(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
    }

    /**
     * Get all sessions.
     *
     * @return Collection of all sessions
     */
    public Collection<SimulationSession> getAllSessions() {
        return sessionStore.getAll();
    }

    /**
     * Remove a session.
     *
     * @param sessionId The session ID
     * @return true if the session was removed
     */
    public boolean removeSession(String sessionId) {
        Optional<SimulationSession> session = sessionStore.remove(sessionId);
        if (session.isPresent()) {
            LOGGER.info("Removed session: " + sessionId);
            return true;
        }
        return false;
    }

    /**
     * Get session status information.
     *
     * @param sessionId The session ID
     * @return Status information as a formatted string
     */
    public String getSessionStatus(String sessionId) {
        SimulationSession session = getSessionOrThrow(sessionId);
        return String.format(
                "Session: %s\nStatus: %s\nSimulation Time: %.2f\nEvent Count: %d\nMeasurements: %d",
                session.getSessionId(),
                session.getStatus(),
                session.getCurrentSimulationTime(),
                session.getEventCount(),
                session.getMeasurementCount()
        );
    }

    /**
     * Shutdown the executor service.
     */
    public void shutdown() {
        executor.shutdown();
    }
}
