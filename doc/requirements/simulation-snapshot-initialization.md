# Requirements: Simulation Snapshot and Initialization

## Goal

Slingshot shall support capturing the distributed simulation state at a point in time and initializing a subsequent simulation run from that state so that the simulation can continue from the captured configuration.

## Scope (initial delivery — PR #52)

This iteration delivers the **contract skeleton** only:

- Type hierarchy and DES events for capture and initialization
- Core barrier orchestration (`CoreSnapshotBehavior`)
- Driver hook for optional initialization snapshot
- Stub `ResourceSimulationSnapshot` (table counts only)

It does **not** yet provide end-to-end checkpointing during a live run.

## Actors

| Actor | Responsibility |
|-------|----------------|
| **Core** | Orchestrates capture barrier, assembles `CompositeSimulationSnapshot`, validates contributor set, schedules initialization event |
| **Snapshot-capable extensions** | Capture extension-owned state into a `SimulationSnapshot`; apply state on initialization |
| **Simulation driver** | Accepts optional initialization snapshot at `init()`; schedules initialization before pre-simulation bootstrap |

## Functional requirements

### FR1 — Extension-owned snapshot types

Each snapshot-capable extension shall define a concrete `SimulationSnapshot` implementation that encodes its runtime state. The core shall not define extension-specific fields.

### FR2 — Composite aggregate

The core shall assemble a `CompositeSimulationSnapshot` that combines `CoreSimulationSnapshot` (engine state) with all active extension snapshots, keyed by `extensionId()`.

### FR3 — Event-driven capture

Snapshot capture shall be coordinated via DES events:

1. `SimulationSnapshotRequested` — starts a capture session
2. `ExtensionSimulationSnapshotCaptured` — extension returns its snapshot
3. `SimulationSnapshotCompleted` — core delivers the assembled composite

### FR4 — Event-driven initialization

State initialization shall occur in the pre-simulation phase:

1. Driver validates the snapshot against active contributors
2. `SimulationStateInitializationRequested` is scheduled before `PreSimulationConfigurationStarted`
3. Extensions respond with `ExtensionSimulationSnapshotInitialized`

### FR5 — Validation

Before initialization, the core shall reject snapshots when:

- Contributor set does not match active snapshot-capable extensions
- A contributor is incompatible with the current simulation context
- A contributor's `schemaVersion` is not supported (major version `1` in this iteration)

Validation failure shall throw `SimulationStateIncompatibleException`.

### FR6 — Unchanged default path

Calling `init(config, monitor)` without a snapshot shall behave identically to the pre-feature driver registration and bootstrap sequence.

### FR7 — Schema versioning

Each `SimulationSnapshot` shall expose `schemaVersion()` (semver string, extension-owned) so snapshot layouts can evolve without silent incompatibility.

## Non-functional requirements

### NFR1 — Incremental extensibility

New extensions shall be able to join snapshot capture by implementing `SnapshotCapableExtension` and adding event handlers, without modifying core snapshot types.

### NFR2 — Event-contract alignment

Snapshot coordination shall use the existing `@OnEvent` / `@Subscribe` / `Result` patterns; the core shall not call extension capture methods directly.

## Non-goals (this iteration)

- `requestSnapshot()` API and engine pause/resume
- Real capture of resource job queues, processor-sharing state, or passive waiting queues
- `SimulationEntityRegistry` and cross-extension entity rebind
- SSJ scheduled-event queue restore (`JobProgressed`, etc.)
- `SimulationJob` workflow wiring for initialization
- Persistence of snapshots to disk

## Future requirements

| ID | Description |
|----|-------------|
| FR-F1 | Core `requestSnapshot()` pauses DES, runs capture barrier, resumes |
| FR-F2 | Resource simulation captures FCFS deques, processor-sharing internals, passive queues |
| FR-F3 | System and usage simulation snapshots for in-flight requests and interpretation contexts |
| FR-F4 | Core restores SSJ scheduled events with re-bound entity references |
| FR-F5 | Workflow job passes initialization snapshot to driver |

## References

- ADR: [0001-simulation-snapshot-and-initialization](../decisions/0001-simulation-snapshot-and-initialization.md)
- User documentation: [Palladio-Documentation-Slingshot](https://github.com/PalladioSimulator/Palladio-Documentation-Slingshot)
