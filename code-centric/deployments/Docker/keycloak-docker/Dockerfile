FROM quay.io/keycloak/keycloak:26.1.4 as builder

# # Enable health and metrics
# ENV KC_HEALTH_ENABLED=true
# ENV KC_METRICS_ENABLED=true

# # Configure database
# ENV KC_DB=postgres
# ENV KC_DB_SCHEMA=keycloak_26_1_4

# # Configure features
# ENV KC_FEATURES=admin-api,account-api,admin-fine-grained-authz

COPY ../*.jar /opt/keycloak/providers/

# Set up the build
WORKDIR /opt/keycloak
RUN /opt/keycloak/bin/kc.sh build --spi-user-cache-default-enable   d=true --http-relative-path=/expkeycloak --health-enabled=true --metrics-enabled=true --db=postgres --features=admin-api,account-api,admin-fine-grained-authz

FROM quay.io/keycloak/keycloak:26.1.4
COPY --from=builder /opt/keycloak /opt/keycloak

# Set up volume
VOLUME /opt/keycloak/data

# Set the web context to /expkeycloak
ENV KC_HTTP_RELATIVE_PATH=/expkeycloak

# Set hostname behavior
ENV KC_HOSTNAME_STRICT=false

# Set up logging
ENV KC_LOG_LEVEL=INFO,com.fk:trace

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--optimized"]