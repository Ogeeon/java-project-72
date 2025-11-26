FROM gradle:8-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle installDist --no-daemon


FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/install/app /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 7070

CMD ["/app/bin/app"]
