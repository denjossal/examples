#!/usr/bin/env bash
# Local developer environment for running the build on macOS + Colima.
#
# This file is git-ignored on purpose: it contains machine-specific paths.
# Usage:
#   source scripts/local-env.sh
#   mvn verify                      # runs everything, including integration tests
#
# CI does NOT use this file — GitHub-hosted Linux runners expose Docker at the
# default socket and run Maven on the JDK selected by setup-java, so no overrides
# are needed there.

# Run Maven (and forked surefire JVMs) on JDK 25, which the project targets.
export JAVA_HOME="$HOME/.sdkman/candidates/java/25-amzn"
export PATH="$JAVA_HOME/bin:$PATH"

# Point Testcontainers at the Colima Docker socket.
export DOCKER_HOST="unix://$HOME/.colima/default/docker.sock"
# Colima mounts the daemon socket at the standard path inside containers, so Ryuk
# (the Testcontainers resource reaper) must bind-mount that path, not DOCKER_HOST.
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="/var/run/docker.sock"

echo "Local env ready: JDK 25 + Colima Docker socket."
