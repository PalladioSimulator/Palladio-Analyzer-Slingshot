package org.palladiosimulator.analyzer.slingshot.mcp.headless;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A no-op implementation of IProgressMonitor for headless execution.
 * All methods do nothing and cancellation is never requested.
 */
public class NullProgressMonitor implements IProgressMonitor {

    private boolean cancelled = false;

    @Override
    public void beginTask(String name, int totalWork) {
        // No-op
    }

    @Override
    public void done() {
        // No-op
    }

    @Override
    public void internalWorked(double work) {
        // No-op
    }

    @Override
    public boolean isCanceled() {
        return cancelled;
    }

    @Override
    public void setCanceled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public void setTaskName(String name) {
        // No-op
    }

    @Override
    public void subTask(String name) {
        // No-op
    }

    @Override
    public void worked(int work) {
        // No-op
    }
}
