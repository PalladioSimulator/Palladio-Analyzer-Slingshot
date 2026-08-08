#!/bin/bash
#
# Slingshot Headless — Example Simulation Runner
#
# This script builds the headless product and runs the MinimalModel example.
# Adjust JAVA_HOME to point to a JDK 17+ installation.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
PRODUCT_DIR="$PROJECT_DIR/releng/org.palladiosimulator.analyzer.slingshot.headless.product"
MODEL_DIR="$SCRIPT_DIR/models/MinimalModel"

# Use provided JAVA_HOME or try to find JDK 17
if [ -z "${JAVA_HOME:-}" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home" ]; then
        JAVA_HOME="/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home"
    else
        echo "Error: JAVA_HOME not set. Please set it to a JDK 17+ installation."
        exit 1
    fi
fi

echo "=== Building Slingshot Headless Product ==="
cd "$PROJECT_DIR"
export JAVA_HOME
mvn clean verify -q -DskipTests

echo ""
echo "=== Extracting Product ==="
PRODUCT_ZIP=$(ls "$PRODUCT_DIR/target/products/"*macosx.cocoa.aarch64.zip 2>/dev/null || true)
if [ -z "$PRODUCT_ZIP" ]; then
    PRODUCT_ZIP=$(ls "$PRODUCT_DIR/target/products/"*.zip 2>/dev/null | head -1)
fi

if [ -z "$PRODUCT_ZIP" ]; then
    echo "Error: No product archive found in $PRODUCT_DIR/target/products/"
    exit 1
fi

EXTRACT_DIR=$(mktemp -d)
unzip -q "$PRODUCT_ZIP" -d "$EXTRACT_DIR"

LAUNCHER_JAR=$(find "$EXTRACT_DIR" -name "org.eclipse.equinox.launcher_*.jar" | head -1)
if [ -z "$LAUNCHER_JAR" ]; then
    echo "Error: Equinox launcher not found in extracted product"
    exit 1
fi

echo ""
echo "=== Running Simulation ==="
java \
    -jar "$LAUNCHER_JAR" \
    -application org.palladiosimulator.analyzer.slingshot.headless.slingshotHeadless \
    -data "$(mktemp -d)" \
    --allocation "$MODEL_DIR/minimal.allocation" \
    --repository "$MODEL_DIR/minimal.repository" \
    --system "$MODEL_DIR/minimal.system" \
    --resourceenvironment "$MODEL_DIR/minimal.resourceenvironment" \
    --usagemodel "$MODEL_DIR/minimal.usagemodel" \
    --simulationTime 10 \
    --seed 12345

echo ""
echo "=== Simulation Complete ==="
rm -rf "$EXTRACT_DIR"
