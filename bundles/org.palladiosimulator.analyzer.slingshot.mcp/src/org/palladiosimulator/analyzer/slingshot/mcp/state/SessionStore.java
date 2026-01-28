package org.palladiosimulator.analyzer.slingshot.mcp.state;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.palladiosimulator.analyzer.slingshot.mcp.runner.SimulationSession;

/**
 * Thread-safe store for simulation sessions.
 * Uses ConcurrentHashMap for safe access from multiple threads.
 */
public class SessionStore {

    private final ConcurrentMap<String, SimulationSession> sessions;

    public SessionStore() {
        this.sessions = new ConcurrentHashMap<>();
    }

    /**
     * Store a session.
     *
     * @param session The session to store
     */
    public void put(SimulationSession session) {
        sessions.put(session.getSessionId(), session);
    }

    /**
     * Get a session by ID.
     *
     * @param sessionId The session ID
     * @return Optional containing the session if found
     */
    public Optional<SimulationSession> get(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    /**
     * Remove a session by ID.
     *
     * @param sessionId The session ID
     * @return Optional containing the removed session if it existed
     */
    public Optional<SimulationSession> remove(String sessionId) {
        return Optional.ofNullable(sessions.remove(sessionId));
    }

    /**
     * Check if a session exists.
     *
     * @param sessionId The session ID
     * @return true if the session exists
     */
    public boolean contains(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    /**
     * Get all sessions.
     *
     * @return Collection of all sessions
     */
    public Collection<SimulationSession> getAll() {
        return sessions.values();
    }

    /**
     * Get the number of stored sessions.
     *
     * @return The session count
     */
    public int size() {
        return sessions.size();
    }

    /**
     * Clear all sessions.
     */
    public void clear() {
        sessions.clear();
    }
}
