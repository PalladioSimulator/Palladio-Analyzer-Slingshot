# Simulation snapshot and initialization via event-driven extension contracts

* Status: accepted
* Date: 2026-06-10
* Deciders: Palladio Slingshot team

## Context and Problem Statement

Slingshot distributes mutable simulation state across behavior extensions: resource tables and job queues, SEFF interpretation contexts, SPD adjustor state, monitors, and the SSJ event queue. There is no unified mechanism to capture that state at a point in time or to initialize a new run from a previously captured state.

How should snapshot capture and state initialization be designed so that extensions can participate incrementally without tight coupling to the core?

## Decision Drivers

* Preserve the existing event-driven architecture (`@OnEvent`, `@Subscribe`, `Result`)
* Let each extension own the shape of its snapshot data; the core must not encode domain-specific fields
* Support evolvable snapshot formats via per-extension `schemaVersion()`
* Enable incremental delivery (skeleton first, real domain capture in follow-up PRs)
* Validate contributor compatibility before initialization to avoid partial or silent state corruption
* Use **initialization** terminology (apply snapshot at pre-sim) rather than **restore** (which implies full runtime rewind)

## Considered Options

### Option 1: Opaque byte fragments with separate manifest

Each extension serializes state into an `ExtensionStateFragment` (byte payload + descriptor). The core stores fragments in a manifest envelope.

* Good, because serialization format is fully extension-controlled
* Bad, because it introduces wrapper types parallel to the snapshot hierarchy and duplicates identity (`descriptor` vs `SimulationSnapshot`)

### Option 2: `SimulationSnapshot` type hierarchy (chosen)

Each contributor implements `SimulationSnapshot`. `CompositeSimulationSnapshot` is the aggregate root. Events carry typed snapshots.

* Good, because extensions add one class per domain; no fragment boilerplate
* Good, because typed lookup (`composite.get(ResourceSimulationSnapshot.class)`) is safe and readable
* Bad, because cross-extension references (jobs → requests) still need a shared entity registry later

### Option 3: Direct driver calls (`extension.captureState()`)

The driver synchronously invokes capture/initialization methods on extensions.

* Good, because simpler control flow
* Bad, because it bypasses the DES event contract model that all other extension interactions use

### Option 4: Separate `*SnapshotBehavior` per extension

Each extension registers a second behavior class for snapshot handlers.

* Good, because separation of concerns in large behavior classes
* Bad, because it requires Guice singleton sharing for mutable state; augmenting the existing behavior is sufficient for now

## Decision Outcome

Chosen option: **Option 2** with **event-driven coordination** (barrier via `CoreSnapshotBehavior`).

* `SimulationSnapshot` is the root interface; each extension defines its own implementation
* `CompositeSimulationSnapshot` aggregates `CoreSimulationSnapshot` and extension snapshots
* Capture: `SimulationSnapshotRequested` → `ExtensionSimulationSnapshotCaptured` → `SimulationSnapshotCompleted`
* Initialization: `SimulationStateInitializationRequested` (pre-simulation) → `ExtensionSimulationSnapshotInitialized`
* Extensions implement `SnapshotCapableExtension` and handle events on their existing behavior class
* `schemaVersion()` uses semver conventions; major version `1` is the compatibility gate in the initial validator

## Consequences

### Positive

* Extensions can adopt snapshot support incrementally by adding a snapshot class and two event handlers
* Core orchestration reuses `CoreBehavior`-style post-intercept rescheduling patterns
* Requirements and API are stable for follow-up work (real queue capture, entity registry, engine pause)

### Negative

* Full checkpointing requires additional PRs: engine pause/resume, entity rebind, SSJ queue restore
* Resource simulation jobs reference system/usage entities; resource-only snapshots cannot resume in-flight work alone
* `schemaVersion` compatibility is coarse (major-prefix check) until per-extension upcasters are implemented

## Links

* Requirements: [simulation-snapshot-initialization.md](../requirements/simulation-snapshot-initialization.md)
* Pull request: [#52](https://github.com/PalladioSimulator/Palladio-Analyzer-Slingshot/pull/52)
