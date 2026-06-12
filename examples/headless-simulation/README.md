# Slingshot Headless — Example: MinimalModel

This directory contains a minimal PCM model set (`MinimalModel`) that you can use to test the Slingshot Headless CLI.

## Model Contents

| File | Type |
|------|------|
| `models/MinimalModel/minimal.allocation` | Allocation |
| `models/MinimalModel/minimal.repository` | Repository |
| `models/MinimalModel/minimal.system` | System |
| `models/MinimalModel/minimal.resourceenvironment` | Resource Environment |
| `models/MinimalModel/minimal.usagemodel` | Usage Model |
| `models/MinimalModel/minimal.monitorrepository` | Monitor Repository |
| `models/MinimalModel/minimal.measuringpoint` | Measuring Point |
| `models/MinimalModel/minimal.spd` | Scaling Policy Definition |

## Prerequisites

- JDK 17+
- The Slingshot headless product ZIP for your platform (build from the project root):
  ```bash
  JAVA_HOME=/path/to/jdk17 mvn clean verify
  # Product ZIPs are in:
  # releng/.../headless.product/target/products/
  ```

## Running

```bash
# 1. Extract the product
unzip path/to/slingshot-headless.product-macosx.cocoa.aarch64.zip
cd Eclipse.app/Contents/Eclipse

# 2. Run with the example model
java -jar plugins/org.eclipse.equinox.launcher_*.jar \
  -application org.palladiosimulator.analyzer.slingshot.headless.slingshotHeadless \
  -data /tmp/slingshot-workspace \
  --allocation path/to/examples/headless-simulation/models/MinimalModel/minimal.allocation \
  --repository path/to/examples/headless-simulation/models/MinimalModel/minimal.repository \
  --system path/to/examples/headless-simulation/models/MinimalModel/minimal.system \
  --resourceenvironment path/to/examples/headless-simulation/models/MinimalModel/minimal.resourceenvironment \
  --usagemodel path/to/examples/headless-simulation/models/MinimalModel/minimal.usagemodel \
  --simulationTime 10 \
  --seed 12345
```

Or use the convenience script:

```bash
# From the project root
./examples/headless-simulation/run.sh
```
