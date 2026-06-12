# Slingshot Headless CLI — Requirements

## Overview

The Slingshot Headless CLI allows running Palladio Architecture Simulator simulations from the command line, without the Eclipse/Palladio Bench IDE.

## Build

```bash
JAVA_HOME=/path/to/jdk17 mvn clean verify
```

The product archives are generated at:
```
releng/org.palladiosimulator.analyzer.slingshot.headless.product/target/products/
├── slingshot-headless.product-macosx.cocoa.aarch64.zip
├── slingshot-headless.product-macosx.cocoa.x86_64.zip
├── slingshot-headless.product-linux.gtk.aarch64.zip
├── slingshot-headless.product-linux.gtk.x86_64.zip
└── slingshot-headless.product-win32.win32.x86_64.zip
```

## Usage

```bash
# Extract the product for your platform
unzip slingshot-headless.product-macosx.cocoa.aarch64.zip
cd Eclipse.app/Contents/Eclipse

# Run a simulation
java -jar plugins/org.eclipse.equinox.launcher_*.jar \
  -application org.palladiosimulator.analyzer.slingshot.headless.slingshotHeadless \
  -data /tmp/workspace \
  --allocation /path/to/model.allocation \
  --repository /path/to/model.repository \
  --system /path/to/model.system \
  --resourceenvironment /path/to/model.resourceenvironment \
  --usagemodel /path/to/model.usagemodel \
  --simulationTime 5000 \
  --seed 42
```

## Arguments

| Argument | Type | Required | Default | Description |
|----------|------|----------|---------|-------------|
| `--allocation <uri>` | file path | No* | — | PCM allocation model |
| `--system <uri>` | file path | No* | — | PCM system model |
| `--repository <uri>` | file path | No* | — | PCM repository model |
| `--resourceenvironment <uri>` | file path | No* | — | PCM resource environment model |
| `--usagemodel <uri>` | file path | No* | — | PCM usage model |
| `--simulationTime <double>` | number | No | `1000` | Simulation end time |
| `--maxMeasurements <int>` | number | No | `100000` | Max measurement count before stopping |
| `--seed <long>` | number | No | (random) | Fixed random seed for reproducibility |
| `--help` | flag | No | — | Print usage and exit |

\* At least one model file argument is required.

## Architecture

```
User (CLI)
  │
  ▼
HeadlessApplication (IApplication)
  ├── Parse CLI args ──────────────────► Map<String, String>
  ├── Create MDSDBlackboard
  ├── PreparePCMBlackboardPartitionJob ──► Initialize PCM partition
  ├── LoadModelIntoBlackboardJob (×N) ──► Load each model file
  ├── Build SimuComConfig from args
  ├── Configure providers (blackboard, partition, config)
  └── SimulationDriver.init() + start()

SimulationDriver
  ├── Create Guice child injector
  ├── Register extensions (behavior, monitoring, SPD, etc.)
  └── SSJ simulation event loop
```

## Dependencies (Runtime)

The product includes (via features):
- Slingshot core, common, eventdriver, workflow
- PCM core behavior extensions (resource, system, usage simulation)
- Monitoring extensions
- SPD interpreter extensions
- All transitive Eclipse/EMF/Guice/Palladio dependencies

Not included (excluded from core feature):
- Eclipse UI/workbench bundles
- Palladio Bench IDE UI
- Debug UI, editors, etc.

## Testing

Unit and integration tests are in `tests/org.palladiosimulator.analyzer.slingshot.headless.test/`:

| Test | What it verifies |
|------|------------------|
| `modelDirectoriesExist` | Test model files are present |
| `slingshotInstanceIsAvailable` | OSGi-initialized Slingshot singleton |
| `minimalModelLoadsIntoBlackboard` | MinimalModel PCM files load into blackboard |
| `usageModelLoadsIntoBlackboard` | UsageModelOnly PCM files load into blackboard |

Full simulation integration tests (requiring all extension bundles) are in the separate `Palladio-Analyzer-Slingshot-E2E-Tests` repository.
