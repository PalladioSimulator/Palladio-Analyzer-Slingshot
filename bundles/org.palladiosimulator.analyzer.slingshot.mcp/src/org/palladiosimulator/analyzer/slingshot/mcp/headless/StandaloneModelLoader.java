package org.palladiosimulator.analyzer.slingshot.mcp.headless;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.analyzer.slingshot.mcp.config.ModelPaths;
import org.palladiosimulator.analyzer.slingshot.mcp.util.EMFStandaloneSetup;

/**
 * Loads PCM models without Eclipse workspace.
 * Creates a ResourceSet with all loaded models for use with the simulation.
 */
public class StandaloneModelLoader {

    private static final Logger LOGGER = Logger.getLogger(StandaloneModelLoader.class);

    private final ModelPaths modelPaths;
    private ResourceSet resourceSet;

    public StandaloneModelLoader(ModelPaths modelPaths) {
        this.modelPaths = modelPaths;
        // Ensure EMF is initialized
        EMFStandaloneSetup.init();
    }

    /**
     * Load all PCM models into a ResourceSet.
     *
     * @return ResourceSet containing all loaded models
     * @throws ModelLoadingException if any required model fails to load
     */
    public ResourceSet loadModels() throws ModelLoadingException {
        LOGGER.info("Loading PCM models from paths: " + modelPaths);

        resourceSet = new ResourceSetImpl();
        List<String> errors = new ArrayList<>();

        // Load all model files
        loadModelIfPresent(resourceSet, modelPaths.getAllocationPath(), "allocation", errors);
        loadModelIfPresent(resourceSet, modelPaths.getUsageModelPath(), "usagemodel", errors);
        loadModelIfPresent(resourceSet, modelPaths.getRepositoryPath(), "repository", errors);
        loadModelIfPresent(resourceSet, modelPaths.getSystemPath(), "system", errors);
        loadModelIfPresent(resourceSet, modelPaths.getResourceEnvironmentPath(), "resourceenvironment", errors);
        loadModelIfPresent(resourceSet, modelPaths.getMonitorRepositoryPath(), "monitorrepository", errors);
        loadModelIfPresent(resourceSet, modelPaths.getMeasuringPointPath(), "measuringpoint", errors);
        loadModelIfPresent(resourceSet, modelPaths.getSloPath(), "slo", errors);
        loadModelIfPresent(resourceSet, modelPaths.getSpdPath(), "spd", errors);

        // Check for required models
        if (modelPaths.getAllocationPath() == null) {
            errors.add("Allocation model path is required");
        }
        if (modelPaths.getUsageModelPath() == null) {
            errors.add("Usage model path is required");
        }

        if (!errors.isEmpty()) {
            throw new ModelLoadingException("Failed to load models: " + String.join(", ", errors));
        }

        LOGGER.info("Successfully loaded " + resourceSet.getResources().size() + " model resources");

        return resourceSet;
    }

    private void loadModelIfPresent(ResourceSet resourceSet, String path, String modelType, List<String> errors) {
        if (path == null || path.isEmpty()) {
            LOGGER.debug("No " + modelType + " model path specified, skipping");
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            errors.add(modelType + " model not found: " + path);
            return;
        }

        try {
            URI uri = URI.createFileURI(file.getAbsolutePath());
            Resource resource = resourceSet.getResource(uri, true);
            if (resource.getErrors().isEmpty()) {
                LOGGER.info("Loaded " + modelType + " model: " + path);
            } else {
                LOGGER.warn("Loaded " + modelType + " model with errors: " + resource.getErrors());
            }
        } catch (Exception e) {
            errors.add("Error loading " + modelType + ": " + e.getMessage());
            LOGGER.error("Failed to load " + modelType + " model: " + path, e);
        }
    }

    /**
     * Get the loaded ResourceSet.
     *
     * @return The ResourceSet, or null if loadModels() hasn't been called
     */
    public ResourceSet getResourceSet() {
        return resourceSet;
    }

    /**
     * Get the file paths as URIs for use with the workflow configuration.
     *
     * @return List of URI strings for all configured model files
     */
    public List<String> getModelFileUris() {
        List<String> uris = new ArrayList<>();

        addUriIfPresent(uris, modelPaths.getAllocationPath());
        addUriIfPresent(uris, modelPaths.getUsageModelPath());
        addUriIfPresent(uris, modelPaths.getRepositoryPath());
        addUriIfPresent(uris, modelPaths.getSystemPath());
        addUriIfPresent(uris, modelPaths.getResourceEnvironmentPath());
        addUriIfPresent(uris, modelPaths.getMonitorRepositoryPath());
        addUriIfPresent(uris, modelPaths.getMeasuringPointPath());
        addUriIfPresent(uris, modelPaths.getSloPath());
        addUriIfPresent(uris, modelPaths.getSpdPath());

        return uris;
    }

    private void addUriIfPresent(List<String> uris, String path) {
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists()) {
                uris.add(URI.createFileURI(file.getAbsolutePath()).toString());
            }
        }
    }

    /**
     * Exception thrown when model loading fails.
     */
    public static class ModelLoadingException extends Exception {
        private static final long serialVersionUID = 1L;

        public ModelLoadingException(String message) {
            super(message);
        }

        public ModelLoadingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
