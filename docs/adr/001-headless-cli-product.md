# ADR 001: Headless CLI Product for Slingshot Simulation

**Date:** 2026-06-12
**Status:** Accepted
**Deciders:** Floriment Klinaku

## Context

The Palladio-Analyzer-Slingshot project provides a discrete-event simulation engine for the Palladio Architecture Simulator. Historically, simulations could only be launched through the Eclipse/Palladio Bench IDE via a "Slingshot Simulation" run configuration (UI-based). This tight coupling to the Eclipse UI framework prevented:

- Integration into CI/CD pipelines and automated regression testing
- Scripted batch simulation runs (e.g., parameter sweeps, sensitivity analysis)
- Usage in non-GUI environments (servers, containers, headless HPC clusters)

The core simulation engine (`org.palladiosimulator.analyzer.slingshot.core`) and its extension system are already decoupled from the UI — they use Google Guice dependency injection and an event-driven architecture. Only the launch plumbing (`SimulationLauncher`, `SimulationWorkflowConfiguration`) depends on Eclipse UI classes like `AbstractPCMLaunchConfigurationDelegate`.

## Decision

We will implement a **headless Eclipse RCP product** that provides a CLI entry point for running Slingshot simulations without the Palladio Bench IDE.

### Approach

1. **New bundle** `org.palladiosimulator.analyzer.slingshot.headless` containing:
   - `HeadlessApplication` implementing `org.eclipse.equinox.app.IApplication`
   - CLI argument parsing for PCM model paths and simulation parameters
   - Direct invocation of `SimulationDriver.init()` and `SimulationDriver.start()` (bypassing Eclipse launch framework)

2. **New product definition** `slingshot-headless.product` configured as an Eclipse `.product` file with `eclipse-repository` packaging via Tycho. The product:
   - Includes the core feature (no UI feature)
   - Includes all extension features (PCM core, monitoring, SPD)
   - Sets `eclipse.application` to the headless application ID
   - Does not include native launchers (runs via `java -jar plugins/org.eclipse.equinox.launcher_*.jar`)

3. **Reuses existing infrastructure**:
   - `PreparePCMBlackboardPartitionJob` and `LoadModelIntoBlackboardJob` for model loading
   - `WorkflowConfigurationModule` static providers for wiring config into Guice
   - `SimuComConfig` with `Map<String, Object>` constructor for configuration

### Non-Goals

- This is NOT a standalone Java executable — it remains an Eclipse RCP application that requires an OSGi runtime
- No changes to the core simulation engine or existing extension mechanism
- No native launchers (no `.exe`/`eclipse` binary) — users invoke via `java -jar`

## Consequences

### Positive
- Simulations can be run from CLI, enabling scripting and automation
- Same simulation engine, same extensions — guaranteed behavioral compatibility
- The headless product can be built alongside the existing update site with no build system changes
- Minimal new code (~120 lines of Java)

### Negative
- Still requires JDK 17+ and the Eclipse Equinox OSGi runtime (~200MB distribution)
- Users must provide the `org.palladiosimulator.pcm.resources` bundle for PrimitiveTypes pathmap resolution (included automatically via feature dependencies)
- Full simulation integration tests require the complete E2E test environment (all extension bundles)

### Neutral
- The product is built and tested as part of the regular Maven/Tycho build
- Platform-specific archives are generated for macOS (aarch64, x86_64), Linux (aarch64, x86_64), and Windows (x86_64)
- Users run with `-application` flag pointing to the headless application ID
