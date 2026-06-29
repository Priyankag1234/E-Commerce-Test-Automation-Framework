# ============================================================
# Dockerfile — Test Runner Container
# Multi-stage build to keep image lean
# ============================================================
# Stage 1: Build — compile and download dependencies
# Stage 2: Runtime — execute tests
# ============================================================

# ─────────────────────────────────────────
# Stage 1: Build Stage
# Downloads Maven dependencies in a cacheable layer
# ─────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy POM first for dependency caching (Docker layer optimization)
COPY pom.xml .

# Download all dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the full source
COPY src ./src
COPY src/main/resources ./src/main/resources

# Compile the project (without running tests)
RUN mvn compile test-compile -B

# ─────────────────────────────────────────
# Stage 2: Runtime Stage
# Runs the tests against the Selenium Grid
# ─────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

# Copy compiled classes and dependencies from build stage
COPY --from=build /app /app
COPY --from=build /root/.m2 /root/.m2

# Create output directories
RUN mkdir -p /app/test-output/reports /app/test-output/screenshots /app/logs

# Environment variable defaults (can be overridden by docker-compose or -e flags)
ENV ENV=qa
ENV BROWSER=chrome
ENV HEADLESS=true
ENV EXECUTION=remote
ENV GRID_URL=http://selenium-hub:4444

# Default command: run all tests in headless remote mode
CMD ["mvn", "clean", "test", \
     "-Denv=${ENV}", \
     "-Dbrowser=${BROWSER}", \
     "-Dheadless=${HEADLESS}", \
     "-Dexecution=${EXECUTION}", \
     "-DgridUrl=${GRID_URL}", \
     "-B"]

# Health signal — successful Maven test exit code = 0
HEALTHCHECK NONE
