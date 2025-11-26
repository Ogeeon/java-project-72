# ============================
# 1. BUILD STAGE
# ============================
FROM gradle:8-jdk17 AS builder

# Create working dir
WORKDIR /app

# Copy everything (Gradle files, wrapper, src/, config/, etc.)
COPY . .

# Build using installDist so we get /build/install/app/
RUN gradle installDist --no-daemon


# ============================
# 2. RUNTIME STAGE
# ============================
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy only the built output from the builder stage
COPY --from=builder /app/build/install/app /app

# Expose port if you want (optional)
EXPOSE 7070

# Start the application
CMD ["/app/bin/app"]
